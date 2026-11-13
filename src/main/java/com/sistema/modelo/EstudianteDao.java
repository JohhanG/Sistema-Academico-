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

    private static final String CONFIG_FILE = "config.properties";
    private static final String DEFAULT_SUPABASE_HOST = "db.qwtdiaeruuxshrmxsaoo.supabase.co";
    private static final String DEFAULT_JDBC_URL =
            "jdbc:postgresql://" + DEFAULT_SUPABASE_HOST + ":5432/postgres?sslmode=require";
    private static final String DEFAULT_USER = "postgres";

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
        if (externalConnection != null && !externalConnection.isClosed()) {
            return externalConnection;
        }

        Properties properties = new Properties();
        File archivoConfig = new File(CONFIG_FILE);
        if (archivoConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(archivoConfig)) {
                properties.load(fis);
            } catch (IOException e) {
                throw new SQLException("Error al leer el archivo " + CONFIG_FILE, e);
            }
        }

        String rawUrl = properties.getProperty("URL",
                properties.getProperty("db.url", DEFAULT_JDBC_URL)).trim();
        String url = normalizarUrlJdbc(rawUrl);
        String user = properties.getProperty("USER",
                properties.getProperty("db.user", DEFAULT_USER)).trim();
        String password = properties.getProperty("PASSWORD",
                properties.getProperty("db.password", "")).trim();

        if (user.isEmpty() && password.isEmpty()) {
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Convierte una URL HTTPS de proyecto Supabase (https://<ref>.supabase.co)
     * en una URL JDBC de PostgreSQL válida si es necesario.
     */
    private String normalizarUrlJdbc(String rawUrl) {
        if (rawUrl == null || rawUrl.isEmpty()) {
            return DEFAULT_JDBC_URL;
        }
        if (rawUrl.startsWith("jdbc:")) {
            return rawUrl;
        }
        if (rawUrl.startsWith("https://") || rawUrl.startsWith("http://")) {
            String host = rawUrl.replaceFirst("^https?://", "").replaceAll("/+$", "");
            if (!host.startsWith("db.") && host.endsWith(".supabase.co")) {
                host = "db." + host;
            }
            return "jdbc:postgresql://" + host + ":5432/postgres?sslmode=require";
        }
        return rawUrl;
    }

    private void cerrarConexionSiEsPropia(Connection conn) {
        if (conn != null && conn != externalConnection) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
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
            throw new RuntimeException("Error al guardar el estudiante en base de datos", e);
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
            throw new RuntimeException("Error al obtener los estudiantes de la base de datos", e);
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
            throw new RuntimeException("Error al buscar el estudiante por ID en la base de datos", e);
        } finally {
            cerrarConexionSiEsPropia(conn);
        }
        return Optional.empty();
    }

    /**
     * Busca un estudiante por su correo electrónico.
     */
    public Optional<Estudiante> buscarPorEmail(String email) {
        String sql = "SELECT id_estudiante, nombre, apellido, email FROM estudiante WHERE email = ?";
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
            throw new RuntimeException("Error al buscar el estudiante por email en la base de datos", e);
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
            throw new RuntimeException("Error al actualizar el estudiante en la base de datos", e);
        } finally {
            cerrarConexionSiEsPropia(conn);
        }
    }

    /**
     * Elimina un estudiante por su identificador único.
     */
    public void eliminar(int id) {
        String sql = "DELETE FROM estudiante WHERE id_estudiante = ?";
        Connection conn = null;

        try {
            conn = obtenerConexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);

                int filas = pstmt.executeUpdate();
                if (filas == 0) {
                    throw new RuntimeException("Estudiante no encontrado con ID: " + id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el estudiante en la base de datos", e);
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
