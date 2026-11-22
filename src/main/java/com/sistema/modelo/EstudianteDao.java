package com.sistema.modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * DAO (Data Access Object) para la entidad Estudiante.
 * Gestiona las operaciones CRUD sobre la tabla 'estudiante' en Supabase (PostgreSQL) usando JDBC.
 * Cumple la regla de arquitectura: sin llamadas a System.out.
 */
public class EstudianteDao {

    private final Connection externalConnection;

    public EstudianteDao() {
        this.externalConnection = null;
    }

    public EstudianteDao(Connection connection) {
        this.externalConnection = connection;
    }

    /**
     * Obtiene una conexión a la base de datos Supabase (PostgreSQL) a partir de la
     * conexión inyectada o leyendo el archivo config.properties en la raíz del proyecto.
     */
    protected Connection obtenerConexion() throws SQLException {
        if (externalConnection != null) {
            if (externalConnection.isClosed()) throw new SQLException("La conexion de prueba esta cerrada.");
            return externalConnection;
        }
        return com.sistema.config.ConexionBD.getConexion();
    }

    private void cerrarConexionSiEsPropia(Connection conn) {
        // La conexion compartida y las conexiones inyectadas pertenecen a quien las crea.
    }

    /**
     * Registra un nuevo estudiante en la base de datos.
     */
    public void guardar(Estudiante estudiante) {
        String sql = "INSERT INTO estudiante (nombre, apellido, email) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, estudiante.getNombre());
                pstmt.setString(2, estudiante.getApellido());
                pstmt.setString(3, estudiante.getEmail());

                pstmt.executeUpdate();

                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        estudiante.setIdEstudiante(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        } finally {
            cerrarConexionSiEsPropia(conn);
        }
    }

    /**
     * Obtiene la lista de todos los estudiantes registrados.
     */
    public List<Estudiante> obtenerTodos() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id_estudiante, nombre, apellido, email FROM estudiante ORDER BY id_estudiante";
        Connection conn = null;

        try {
            conn = obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearEstudiante(rs));
                }
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        } finally {
            cerrarConexionSiEsPropia(conn);
        }
        return lista;
    }

    /**
     * Busca un estudiante por su identificador único.
     */
    public Optional<Estudiante> buscarPorId(int id) {
        String sql = "SELECT id_estudiante, nombre, apellido, email FROM estudiante WHERE id_estudiante = ?";
        Connection conn = null;

        try {
            conn = obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapearEstudiante(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        } finally {
            cerrarConexionSiEsPropia(conn);
        }
        return Optional.empty();
    }

    /**
     * Busca un estudiante por su correo electrónico.
     */
    public Optional<Estudiante> buscarPorEmail(String email) {
        String sql = "SELECT id_estudiante, nombre, apellido, email FROM estudiante WHERE LOWER(email) = LOWER(?)";
        Connection conn = null;

        try {
            conn = obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapearEstudiante(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        } finally {
            cerrarConexionSiEsPropia(conn);
        }
        return Optional.empty();
    }

    /**
     * Actualiza los datos de un estudiante existente.
     */
    public void actualizar(Estudiante estudiante) {
        String sql = "UPDATE estudiante SET nombre = ?, apellido = ?, email = ? WHERE id_estudiante = ?";
        Connection conn = null;

        try {
            conn = obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, estudiante.getNombre());
                pstmt.setString(2, estudiante.getApellido());
                pstmt.setString(3, estudiante.getEmail());
                pstmt.setInt(4, estudiante.getIdEstudiante());

                int filas = pstmt.executeUpdate();
                if (filas == 0) {
                    throw new RuntimeException("Estudiante no encontrado con ID: " + estudiante.getIdEstudiante());
                }
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        } finally {
            cerrarConexionSiEsPropia(conn);
        }
    }

    /**
     * Elimina un estudiante por su identificador único.
     */
    public void eliminar(int id) {
        String sql = "DELETE FROM estudiante WHERE id_estudiante = ? AND NOT EXISTS (SELECT 1 FROM inscripcion_curso d WHERE d.id_estudiante = estudiante.id_estudiante) AND NOT EXISTS (SELECT 1 FROM usuario d WHERE d.id_estudiante = estudiante.id_estudiante)";
        Connection conn = null;

        try {
            conn = obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);

                int filas = pstmt.executeUpdate();
                if (filas == 0) {
                    throw new IllegalArgumentException("No se puede eliminar: el estudiante no existe o tiene matriculas o una cuenta asociada.");
                }
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        } finally {
            cerrarConexionSiEsPropia(conn);
        }
    }

    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante();
        estudiante.setIdEstudiante(rs.getInt("id_estudiante"));
        estudiante.setNombre(rs.getString("nombre"));
        estudiante.setApellido(rs.getString("apellido"));
        estudiante.setEmail(rs.getString("email"));
        return estudiante;
    }
}
