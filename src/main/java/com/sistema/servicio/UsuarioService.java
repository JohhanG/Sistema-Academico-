package com.sistema.servicio;

import com.sistema.config.SeguridadUtil;
import com.sistema.modelo.Usuario;
import com.sistema.modelo.UsuarioDAO;
import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;
import com.sistema.modelo.Docente;
import com.sistema.modelo.DocenteDAO;

import java.util.List;

// Servicio para validar usuarios, roles y encriptar contrasenas con SHA-512
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;
    private final EstudianteDao estudianteDAO;
    private final DocenteDAO docenteDAO;

    public UsuarioService() {
        this(new UsuarioDAO());
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this(usuarioDAO, new EstudianteDao(), new DocenteDAO());
    }

    public UsuarioService(UsuarioDAO usuarioDAO, EstudianteDao estudianteDAO, DocenteDAO docenteDAO) {
        if (usuarioDAO == null || estudianteDAO == null || docenteDAO == null) {
            throw new IllegalArgumentException("Los DAO no pueden ser nulos.");
        }
        this.usuarioDAO = usuarioDAO;
        this.estudianteDAO = estudianteDAO;
        this.docenteDAO = docenteDAO;
    }

    public List<Estudiante> listarEstudiantes() { return estudianteDAO.obtenerTodos(); }
    public List<Docente> listarDocentes() { return docenteDAO.listar(); }

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

        Usuario existente = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (existente != null && existente.getIdUsuario() != usuario.getIdUsuario()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");
        }
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
        Integer idEstudiante = usuario.getIdEstudiante();
        Integer idDocente = usuario.getIdDocente();
        if (rol.equals("ADMIN")) {
            if (idEstudiante != null || idDocente != null) {
                throw new IllegalArgumentException("Un administrador no debe tener un registro academico asociado.");
            }
        } else if (rol.equals("ESTUDIANTE")) {
            if (idEstudiante == null || idEstudiante <= 0 || idDocente != null) {
                throw new IllegalArgumentException("Selecciona un estudiante y deja vacio el docente.");
            }
            if (estudianteDAO.buscarPorId(idEstudiante).isEmpty()) {
                throw new IllegalArgumentException("El estudiante seleccionado no existe o no se pudo consultar.");
            }
        } else {
            if (idDocente == null || idDocente <= 0 || idEstudiante != null) {
                throw new IllegalArgumentException("Selecciona un docente y deja vacio el estudiante.");
            }
            if (docenteDAO.buscarPorId(idDocente) == null) {
                throw new IllegalArgumentException("El docente seleccionado no existe o no se pudo consultar.");
            }
        }
        if ((idEstudiante != null || idDocente != null)
                && usuarioDAO.existeVinculoEnOtraCuenta(idEstudiante, idDocente, usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El registro academico ya esta asociado a otra cuenta.");
        }
    }
}
