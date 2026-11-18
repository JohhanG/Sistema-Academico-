package com.sistema.modelo;

import com.sistema.config.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO para las operaciones CRUD de la tabla materia
public class MateriaDAO {

    public boolean insertar(Materia materia) {
        String sql = "INSERT INTO materia (nombre_materia, creditos) VALUES (?, ?)";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, materia.getNombreMateria());
            ps.setInt(2, materia.getCreditos());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        materia.setIdMateria(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar materia: " + e.getMessage());
        }
        return false;
    }

    public List<Materia> listar() {
        List<Materia> lista = new ArrayList<>();
        String sql = "SELECT id_materia, nombre_materia, creditos FROM materia ORDER BY id_materia ASC";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
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
            System.err.println("Error al listar materias: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Materia materia) {
        String sql = "UPDATE materia SET nombre_materia = ?, creditos = ? WHERE id_materia = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, materia.getNombreMateria());
            ps.setInt(2, materia.getCreditos());
            ps.setInt(3, materia.getIdMateria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar materia: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int idMateria) {
        String sql = "DELETE FROM materia WHERE id_materia = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMateria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar materia: " + e.getMessage());
        }
        return false;
    }

    public Materia buscarPorId(int idMateria) {
        String sql = "SELECT id_materia, nombre_materia, creditos FROM materia WHERE id_materia = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
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
            System.err.println("Error al buscar materia: " + e.getMessage());
        }
        return null;
    }
}
