
package com.sistema.modelo;

import com.sistema.config.conexionbd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    public int insertar(Grupo grupo) throws SQLException {
        String sql = "INSERT INTO grupo (id_materia, id_docente, aula, horario) VALUES (?, ?, ?, ?)";
        try (Connection con = conexionbd.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, grupo.getIdMateria());
            ps.setInt(2, grupo.getIdDocente());
            ps.setString(3, grupo.getAula());
            ps.setString(4, grupo.getHorario());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    grupo.setIdGrupo(rs.getInt(1));
                    return grupo.getIdGrupo();
                }
            }
        }
        return -1;
    }

    public List<Grupo> listarTodos() throws SQLException {
        List<Grupo> grupos = new ArrayList<>();
        String sql = "SELECT id_grupo, id_materia, id_docente, aula, horario FROM grupo ORDER BY id_grupo";
        try (Connection con = conexionbd.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                grupos.add(mapear(rs));
            }
        }
        return grupos;
    }

    public Grupo buscarPorId(int idGrupo) throws SQLException {
        String sql = "SELECT id_grupo, id_materia, id_docente, aula, horario FROM grupo WHERE id_grupo = ?";
        try (Connection con = conexionbd.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public boolean actualizar(Grupo grupo) throws SQLException {
        String sql = "UPDATE grupo SET id_materia = ?, id_docente = ?, aula = ?, horario = ? WHERE id_grupo = ?";
        try (Connection con = conexionbd.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, grupo.getIdMateria());
            ps.setInt(2, grupo.getIdDocente());
            ps.setString(3, grupo.getAula());
            ps.setString(4, grupo.getHorario());
            ps.setInt(5, grupo.getIdGrupo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idGrupo) throws SQLException {
        String sql = "DELETE FROM grupo WHERE id_grupo = ?";
        try (Connection con = conexionbd.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idGrupo);
            return ps.executeUpdate() > 0;
        }
    }

    private Grupo mapear(ResultSet rs) throws SQLException {
        return new Grupo(
                rs.getInt("id_grupo"),
                rs.getInt("id_materia"),
                rs.getInt("id_docente"),
                rs.getString("aula"),
                rs.getString("horario"));
    }
}