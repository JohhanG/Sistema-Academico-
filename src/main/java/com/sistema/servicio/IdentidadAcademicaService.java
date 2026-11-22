package com.sistema.servicio;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;
import com.sistema.modelo.Usuario;

/** Resuelve la identidad exclusivamente mediante la relacion explicita de la cuenta. */
public class IdentidadAcademicaService {
    private final EstudianteDao estudianteDAO;

    public IdentidadAcademicaService() { this(new EstudianteDao()); }

    public IdentidadAcademicaService(EstudianteDao estudianteDAO) {
        if (estudianteDAO == null) throw new IllegalArgumentException("El DAO de estudiante no puede ser nulo.");
        this.estudianteDAO = estudianteDAO;
    }

    public Estudiante exigirEstudiante(Usuario usuario) {
        if (usuario == null || !"ESTUDIANTE".equalsIgnoreCase(usuario.getRol())
                || usuario.getIdEstudiante() == null || usuario.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("La cuenta no tiene un estudiante asociado. Solicita al administrador que la vincule.");
        }
        return estudianteDAO.buscarPorId(usuario.getIdEstudiante()).orElseThrow(() ->
                new IllegalArgumentException("No se pudo encontrar el estudiante asociado. Revisa la conexion y el vinculo de la cuenta."));
    }
}
