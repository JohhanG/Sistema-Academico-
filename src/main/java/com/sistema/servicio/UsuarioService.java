package com.sistema.servicio;

import com.sistema.config.SeguridadUtil;
import com.sistema.modelo.Usuario;
import com.sistema.modelo.UsuarioDAO;

import java.util.List;

// Servicio para validar usuarios, roles y encriptar contrasenas con SHA-512
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        if (usuarioDAO == null) {
            throw new IllegalArgumentException("El DAO de usuario no puede ser nulo.");
        }
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contrasena es obligatoria.");
        }
        return usuarioDAO.autenticar(username.trim(), password);
    }

    public boolean registrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        String usernameLimpio = usuario.getUsername().trim();
        usuario.setUsername(usernameLimpio);
        usuario.setRol(usuario.getRol().trim().toUpperCase());

        Usuario existente = usuarioDAO.buscarPorUsername(usernameLimpio);
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");
        }

        if (!SeguridadUtil.esHashValido(usuario.getPassword())) {
            usuario.setPassword(SeguridadUtil.hashSHA512(usuario.getPassword()));
        }

        return usuarioDAO.insertar(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    public Usuario buscarPorId(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser mayor a cero.");
        }
        return usuarioDAO.buscarPorId(idUsuario);
    }

    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID de usuario no es valido.");
        }
        validarUsuario(usuario);
        usuario.setUsername(usuario.getUsername().trim());
        usuario.setRol(usuario.getRol().trim().toUpperCase());

        if (!SeguridadUtil.esHashValido(usuario.getPassword())) {
            usuario.setPassword(SeguridadUtil.hashSHA512(usuario.getPassword()));
        }

        return usuarioDAO.actualizar(usuario);
    }

    public boolean eliminarUsuario(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser mayor a cero.");
        }
        return usuarioDAO.eliminar(idUsuario);
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contrasena es obligatoria.");
        }
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }
        String rol = usuario.getRol().trim().toUpperCase();
        if (!rol.equals("ADMIN") && !rol.equals("DOCENTE") && !rol.equals("ESTUDIANTE")) {
            throw new IllegalArgumentException("El rol debe ser ADMIN, DOCENTE o ESTUDIANTE.");
        }
    }
}
