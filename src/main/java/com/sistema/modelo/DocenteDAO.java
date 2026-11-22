package com.sistema.modelo;

import com.sistema.config.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO para las operaciones CRUD de la tabla docente
public class DocenteDAO {

    public boolean insertar(Docente docente) {
        String sql = "INSERT INTO docente (nombre, especialidad) VALUES (?, ?)";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, docente.getNombre());
            ps.setString(2, docente.getEspecialidad());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        docente.setIdDocente(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
        return false;
    }

    public List<Docente> listar() {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT id_docente, nombre, especialidad FROM docente ORDER BY id_docente ASC";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
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
            throw com.sistema.config.ErrorDatos.desde(e);
        }
        return lista;
    }

    public boolean actualizar(Docente docente) {
        String sql = "UPDATE docente SET nombre = ?, especialidad = ? WHERE id_docente = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, docente.getNombre());
            ps.setString(2, docente.getEspecialidad());
            ps.setInt(3, docente.getIdDocente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
    }

    public boolean eliminar(int idDocente) {
        String sql = "DELETE FROM docente WHERE id_docente = ? AND NOT EXISTS (SELECT 1 FROM grupo d WHERE d.id_docente = docente.id_docente) AND NOT EXISTS (SELECT 1 FROM usuario d WHERE d.id_docente = docente.id_docente)";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDocente);
            if (ps.executeUpdate() == 0) throw new IllegalArgumentException("No se puede eliminar: el registro no existe o tiene informacion academica asociada.");
            return true;
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
    }

    public Docente buscarPorId(int idDocente) {
        String sql = "SELECT id_docente, nombre, especialidad FROM docente WHERE id_docente = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
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
            throw com.sistema.config.ErrorDatos.desde(e);
        }
        return null;
    }
}
