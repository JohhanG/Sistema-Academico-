package com.sistema.servicio;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;

import java.util.List;
import java.util.Optional;

// Servicio con las validaciones de negocio para Estudiante
public class EstudianteService {

    private final EstudianteDao estudianteDao;

    public EstudianteService() {
        this.estudianteDao = new EstudianteDao();
    }

    public EstudianteService(EstudianteDao estudianteDao) {
        if (estudianteDao == null) {
            throw new IllegalArgumentException("El DAO de estudiante no puede ser nulo.");
        }
        this.estudianteDao = estudianteDao;
    }

    public Estudiante registrarEstudiante(String nombre, String apellido, String email) {
        Estudiante estudiante = new Estudiante(
                nombre != null ? nombre.trim() : null,
                apellido != null ? apellido.trim() : null,
                email != null ? email.trim() : null
        );
        registrarEstudiante(estudiante);
        return estudiante;
    }

    public void registrarEstudiante(Estudiante estudiante) {
        validarEstudiante(estudiante);

        Optional<Estudiante> existente = estudianteDao.buscarPorEmail(estudiante.getEmail());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un estudiante con el email: " + estudiante.getEmail());
        }

        estudianteDao.guardar(estudiante);
    }

    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del estudiante debe ser mayor a cero.");
        }
        return estudianteDao.buscarPorId(id);
    }

    public Optional<Estudiante> buscarEstudiantePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacio.");
        }
        return estudianteDao.buscarPorEmail(email.trim());
    }

    public void actualizarEstudiante(int id, String nombre, String apellido, String email) {
        Estudiante estudiante = new Estudiante(
                id,
                nombre != null ? nombre.trim() : null,
                apellido != null ? apellido.trim() : null,
                email != null ? email.trim() : null
        );
        actualizarEstudiante(estudiante);
    }

    public void actualizarEstudiante(Estudiante estudiante) {
        if (estudiante == null || estudiante.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("El estudiante o su ID no es valido.");
        }

        validarEstudiante(estudiante);

        Optional<Estudiante> existente = estudianteDao.buscarPorId(estudiante.getIdEstudiante());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No se encontro el estudiante con ID: " + estudiante.getIdEstudiante());
        }

        Optional<Estudiante> porEmail = estudianteDao.buscarPorEmail(estudiante.getEmail());
        if (porEmail.isPresent() && porEmail.get().getIdEstudiante() != estudiante.getIdEstudiante()) {
            throw new IllegalArgumentException("El email ya esta en uso por otro estudiante.");
        }

        estudianteDao.actualizar(estudiante);
    }

    public void eliminarEstudiante(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del estudiante debe ser mayor a cero.");
        }

        Optional<Estudiante> existente = estudianteDao.buscarPorId(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No se encontro el estudiante con ID: " + id);
        }

        estudianteDao.eliminar(id);
    }

    private void validarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (estudiante.getNombre() == null || estudiante.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (estudiante.getApellido() == null || estudiante.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (estudiante.getEmail() == null || estudiante.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }
        String correo = estudiante.getEmail().trim();
        if (!correo.contains("@") || !correo.contains(".")) {
            throw new IllegalArgumentException("El formato del email no es valido.");
        }
    }
}
