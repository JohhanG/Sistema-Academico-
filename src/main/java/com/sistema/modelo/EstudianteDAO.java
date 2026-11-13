package com.sistema.modelo;

import com.sistema.config.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de Acceso a Datos (DAO) para la entidad Estudiante.
 * Implementa las operaciones CRUD contra la base de datos PostgreSQL en Supabase.
 */
public class EstudianteDAO {

    /**
     * Inserta un nuevo estudiante en la base de datos.
     *
     * @param estudiante Objeto Estudiante con nombre, apellido y email.
     * @return true si se insertó con éxito, false en caso contrario.
     */
    public boolean insertar(Estudiante estudiante) {
        String sql = "INSERT INTO estudiante (nombre, apellido, email) VALUES (?, ?, ?)";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para insertar el estudiante.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getApellido());
            ps.setString(3, estudiante.getEmail());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        estudiante.setIdEstudiante(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar estudiante: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene la lista de todos los estudiantes registrados.
     *
     * @return Lista de estudiantes ordenados por ID ascendente.
     */
    public List<Estudiante> listar() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id_estudiante, nombre, apellido, email FROM estudiante ORDER BY id_estudiante ASC";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para listar estudiantes.");
            return lista;
        }

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Estudiante est = new Estudiante(
                        rs.getInt("id_estudiante"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email")
                );
                lista.add(est);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar estudiantes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza la información de un estudiante existente.
     *
     * @param estudiante Objeto Estudiante con los datos modificados.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    public boolean actualizar(Estudiante estudiante) {
        String sql = "UPDATE estudiante SET nombre = ?, apellido = ?, email = ? WHERE id_estudiante = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para actualizar el estudiante.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getApellido());
            ps.setString(3, estudiante.getEmail());
            ps.setInt(4, estudiante.getIdEstudiante());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estudiante: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina un estudiante de la base de datos por su ID.
     *
     * @param idEstudiante ID del estudiante a eliminar.
     * @return true si se eliminó con éxito, false en caso contrario.
     */
    public boolean eliminar(int idEstudiante) {
        String sql = "DELETE FROM estudiante WHERE id_estudiante = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para eliminar el estudiante.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar estudiante: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca un estudiante específico por su ID.
     *
     * @param idEstudiante ID del estudiante.
     * @return Objeto Estudiante si se encuentra, o null si no existe.
     */
    public Estudiante buscarPorId(int idEstudiante) {
        String sql = "SELECT id_estudiante, nombre, apellido, email FROM estudiante WHERE id_estudiante = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para buscar estudiante.");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Estudiante(
                            rs.getInt("id_estudiante"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar estudiante por ID: " + e.getMessage());
        }
        return null;
    }
}

