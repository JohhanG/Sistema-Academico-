package com.sistema.controlador;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Controlador encargado de orquestar las operaciones de negocio y persistencia
 * para la entidad Estudiante entre la Vista y el Modelo/DAO.
 */
public class EstudianteController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final EstudianteDao estudianteDao;

    public EstudianteController() {
        this.estudianteDao = new EstudianteDao();
    }

    public EstudianteController(EstudianteDao estudianteDao) {
        if (estudianteDao == null) {
            throw new IllegalArgumentException("El DAO de estudiante no puede ser nulo.");
        }
        this.estudianteDao = estudianteDao;
    }

    /**
     * Registra un nuevo estudiante a partir de sus datos básicos.
     */
    public Estudiante registrarEstudiante(String nombre, String apellido, String email) {
        Estudiante estudiante = new Estudiante(
                nombre != null ? nombre.trim() : null,
                apellido != null ? apellido.trim() : null,
                email != null ? email.trim() : null
        );
        registrarEstudiante(estudiante);
        return estudiante;
    }

    /**
     * Valida y registra un objeto Estudiante en la base de datos.
     */
    public void registrarEstudiante(Estudiante estudiante) {
        validarEstudiante(estudiante);

        Optional<Estudiante> existente = estudianteDao.buscarPorEmail(estudiante.getEmail());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el email: " + estudiante.getEmail());
        }

        estudianteDao.guardar(estudiante);
    }

    /**
     * Obtiene todos los estudiantes registrados.
     */
    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    /**
     * Busca un estudiante por su ID.
     */
    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del estudiante debe ser mayor a cero.");
        }
        return estudianteDao.buscarPorId(id);
    }

    /**
     * Busca un estudiante por su correo electrónico.
     */
    public Optional<Estudiante> buscarEstudiantePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email de búsqueda no puede estar vacío.");
        }
        return estudianteDao.buscarPorEmail(email.trim());
    }

    /**
     * Actualiza los datos de un estudiante existente.
     */
    public void actualizarEstudiante(int id, String nombre, String apellido, String email) {
        Estudiante estudiante = new Estudiante(
                id,
                nombre != null ? nombre.trim() : null,
                apellido != null ? apellido.trim() : null,
                email != null ? email.trim() : null
        );
        actualizarEstudiante(estudiante);
    }

    /**
     * Valida y actualiza un objeto Estudiante existente.
     */
    public void actualizarEstudiante(Estudiante estudiante) {
        if (estudiante == null || estudiante.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("El estudiante o su ID es inválido para actualizar.");
        }

        validarEstudiante(estudiante);

        Optional<Estudiante> existente = estudianteDao.buscarPorId(estudiante.getIdEstudiante());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el estudiante con ID: " + estudiante.getIdEstudiante());
        }

        Optional<Estudiante> porEmail = estudianteDao.buscarPorEmail(estudiante.getEmail());
        if (porEmail.isPresent() && porEmail.get().getIdEstudiante() != estudiante.getIdEstudiante()) {
            throw new IllegalArgumentException("El email ya está en uso por otro estudiante: " + estudiante.getEmail());
        }

        estudianteDao.actualizar(estudiante);
    }

    /**
     * Elimina un estudiante por su ID previa verificación de existencia.
     */
    public void eliminarEstudiante(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del estudiante debe ser mayor a cero.");
        }

        Optional<Estudiante> existente = estudianteDao.buscarPorId(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el estudiante con ID: " + id);
        }

        estudianteDao.eliminar(id);
    }

    private void validarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (estudiante.getNombre() == null || estudiante.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estudiante es obligatorio.");
        }
        if (estudiante.getApellido() == null || estudiante.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del estudiante es obligatorio.");
        }
        if (estudiante.getEmail() == null || estudiante.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del estudiante es obligatorio.");
        }
        if (!EMAIL_PATTERN.matcher(estudiante.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("El formato del email no es válido.");
        }
    }
}
