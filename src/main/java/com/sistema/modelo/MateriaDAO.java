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
 * Objeto de Acceso a Datos (DAO) para la entidad Materia.
 * Gestiona las operaciones CRUD sobre la tabla 'materia' en Supabase (PostgreSQL).
 */
public class MateriaDAO {

    /**
     * Inserta una nueva materia en la base de datos.
     *
     * @param materia Objeto Materia con nombreMateria y creditos.
     * @return true si se insertó con éxito, false en caso contrario.
     */
    public boolean insertar(Materia materia) {
        String sql = "INSERT INTO materia (nombre_materia, creditos) VALUES (?, ?)";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para insertar la materia.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, materia.getNombreMateria());
            ps.setInt(2, materia.getCreditos());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        materia.setIdMateria(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar materia: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene la lista de todas las materias registradas.
     *
     * @return Lista de materias ordenadas por ID ascendente.
     */
    public List<Materia> listar() {
        List<Materia> lista = new ArrayList<>();
        String sql = "SELECT id_materia, nombre_materia, creditos FROM materia ORDER BY id_materia ASC";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para listar materias.");
            return lista;
        }

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Materia mat = new Materia(
                        rs.getInt("id_materia"),
                        rs.getString("nombre_materia"),
                        rs.getInt("creditos")
                );
                lista.add(mat);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar materias: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza los datos de una materia existente.
     *
     * @param materia Objeto Materia con los datos modificados.
     * @return true si se actualizó con éxito, false en caso contrario.
     */
    public boolean actualizar(Materia materia) {
        String sql = "UPDATE materia SET nombre_materia = ?, creditos = ? WHERE id_materia = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para actualizar la materia.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, materia.getNombreMateria());
            ps.setInt(2, materia.getCreditos());
            ps.setInt(3, materia.getIdMateria());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar materia: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina una materia de la base de datos por su ID.
     *
     * @param idMateria ID de la materia a eliminar.
     * @return true si se eliminó con éxito, false en caso contrario.
     */
    public boolean eliminar(int idMateria) {
        String sql = "DELETE FROM materia WHERE id_materia = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para eliminar la materia.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMateria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar materia: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca una materia por su ID.
     *
     * @param idMateria ID de la materia.
     * @return Objeto Materia si existe, o null en caso contrario.
     */
    public Materia buscarPorId(int idMateria) {
        String sql = "SELECT id_materia, nombre_materia, creditos FROM materia WHERE id_materia = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para buscar materia.");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMateria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Materia(
                            rs.getInt("id_materia"),
                            rs.getString("nombre_materia"),
                            rs.getInt("creditos")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar materia por ID: " + e.getMessage());
        }
        return null;
    }
}
