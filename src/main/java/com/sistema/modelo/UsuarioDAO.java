package com.sistema.modelo;

import com.sistema.config.ConexionBD;
import com.sistema.config.SeguridadUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// DAO para gestionar usuarios y login en la tabla usuario
public class UsuarioDAO {

    public Usuario autenticar(String username, String passwordPlano) {
        Usuario usuario = buscarPorUsername(username);
        if (usuario == null) {
            return null;
        }
        if (SeguridadUtil.verificarPassword(passwordPlano, usuario.getPassword())) {
            return usuario;
        }
        return null;
    }

    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT id_usuario, username, password, rol, id_estudiante, id_docente FROM usuario WHERE username = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarPorId(int idUsuario) {
        String sql = "SELECT id_usuario, username, password, rol, id_estudiante, id_docente FROM usuario WHERE id_usuario = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuario (username, password, rol, id_estudiante, id_docente) VALUES (?, ?, ?, ?, ?)";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        String passwordHash = usuario.getPassword();
        if (!SeguridadUtil.esHashValido(passwordHash)) {
            passwordHash = SeguridadUtil.hashSHA512(passwordHash);
            usuario.setPassword(passwordHash);
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, passwordHash);
            ps.setString(3, usuario.getRol());
            if (usuario.getIdEstudiante() != null) {
                ps.setInt(4, usuario.getIdEstudiante());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            if (usuario.getIdDocente() != null) {
                ps.setInt(5, usuario.getIdDocente());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setIdUsuario(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, username, password, rol, id_estudiante, id_docente FROM usuario ORDER BY id_usuario ASC";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return lista;
        }

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET username = ?, password = ?, rol = ?, id_estudiante = ?, id_docente = ? WHERE id_usuario = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        String passwordHash = usuario.getPassword();
        if (!SeguridadUtil.esHashValido(passwordHash)) {
            passwordHash = SeguridadUtil.hashSHA512(passwordHash);
            usuario.setPassword(passwordHash);
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, passwordHash);
            ps.setString(3, usuario.getRol());
            if (usuario.getIdEstudiante() != null) {
                ps.setInt(4, usuario.getIdEstudiante());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            if (usuario.getIdDocente() != null) {
                ps.setInt(5, usuario.getIdDocente());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setInt(6, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
        return false;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));

        int idEst = rs.getInt("id_estudiante");
        u.setIdEstudiante(rs.wasNull() ? null : idEst);

        int idDoc = rs.getInt("id_docente");
        u.setIdDocente(rs.wasNull() ? null : idDoc);
        return u;
    }
}
