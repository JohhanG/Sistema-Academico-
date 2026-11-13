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
 * Objeto de Acceso a Datos (DAO) para la entidad Docente.
 * Gestiona las operaciones CRUD sobre la tabla 'docente' en Supabase (PostgreSQL).
 */
public class DocenteDAO {

    /**
     * Inserta un nuevo docente en la base de datos.
     *
     * @param docente Objeto Docente con nombre y especialidad.
     * @return true si se insertó con éxito, false en caso contrario.
     */
    public boolean insertar(Docente docente) {
        String sql = "INSERT INTO docente (nombre, especialidad) VALUES (?, ?)";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para insertar el docente.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, docente.getNombre());
            ps.setString(2, docente.getEspecialidad());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        docente.setIdDocente(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar docente: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene la lista de todos los docentes registrados.
     *
     * @return Lista de docentes ordenados por ID ascendente.
     */
    public List<Docente> listar() {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT id_docente, nombre, especialidad FROM docente ORDER BY id_docente ASC";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para listar docentes.");
            return lista;
        }

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Docente doc = new Docente(
                        rs.getInt("id_docente"),
                        rs.getString("nombre"),
                        rs.getString("especialidad")
                );
                lista.add(doc);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar docentes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza la información de un docente existente.
     *
     * @param docente Objeto Docente con los datos modificados.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    public boolean actualizar(Docente docente) {
        String sql = "UPDATE docente SET nombre = ?, especialidad = ? WHERE id_docente = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para actualizar el docente.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, docente.getNombre());
            ps.setString(2, docente.getEspecialidad());
            ps.setInt(3, docente.getIdDocente());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar docente: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina un docente de la base de datos por su ID.
     *
     * @param idDocente ID del docente a eliminar.
     * @return true si se eliminó con éxito, false en caso contrario.
     */
    public boolean eliminar(int idDocente) {
        String sql = "DELETE FROM docente WHERE id_docente = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para eliminar el docente.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDocente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar docente: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca un docente por su ID.
     *
     * @param idDocente ID del docente.
     * @return Objeto Docente si existe, o null en caso contrario.
     */
    public Docente buscarPorId(int idDocente) {
        String sql = "SELECT id_docente, nombre, especialidad FROM docente WHERE id_docente = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para buscar docente.");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDocente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Docente(
                            rs.getInt("id_docente"),
                            rs.getString("nombre"),
                            rs.getString("especialidad")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar docente por ID: " + e.getMessage());
        }
        return null;
    }
}
