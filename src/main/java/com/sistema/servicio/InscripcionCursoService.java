package com.sistema.servicio;

import com.sistema.modelo.InscripcionCurso;
import com.sistema.modelo.InscripcionCursoDAO;

import java.util.List;

// Servicio con las validaciones de negocio para matriculas y notas
public class InscripcionCursoService {

    private final InscripcionCursoDAO inscripcionDAO;

    public InscripcionCursoService() {
        this.inscripcionDAO = new InscripcionCursoDAO();
    }

    public InscripcionCursoService(InscripcionCursoDAO inscripcionDAO) {
        if (inscripcionDAO == null) {
            throw new IllegalArgumentException("El DAO de inscripcion no puede ser nulo.");
        }
        this.inscripcionDAO = inscripcionDAO;
    }

    public boolean matricularEstudiante(InscripcionCurso inscripcion) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("La inscripcion no puede ser nula.");
        }
        if (inscripcion.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("El ID del estudiante debe ser mayor a cero.");
        }
        if (inscripcion.getIdGrupo() <= 0) {
            throw new IllegalArgumentException("El ID del grupo debe ser mayor a cero.");
        }
        if (inscripcionDAO.existeMatricula(inscripcion.getIdEstudiante(), inscripcion.getIdGrupo())) {
            throw new IllegalArgumentException("El estudiante ya esta matriculado en este grupo.");
        }

        validarNotaYEstado(inscripcion);
        return inscripcionDAO.matricular(inscripcion);
    }

    public List<InscripcionCurso> listarInscripciones() {
        return inscripcionDAO.listar();
    }

    public List<InscripcionCurso> listarPorEstudiante(int idEstudiante) {
        if (idEstudiante <= 0) {
            throw new IllegalArgumentException("El ID del estudiante debe ser mayor a cero.");
        }
        return inscripcionDAO.listarPorEstudiante(idEstudiante);
    }

    public boolean actualizarCalificacion(InscripcionCurso inscripcion) {
        if (inscripcion == null || inscripcion.getIdInscripcion() <= 0) {
            throw new IllegalArgumentException("El ID de inscripcion no es valido.");
        }
        validarNotaYEstado(inscripcion);
        return inscripcionDAO.actualizar(inscripcion);
    }

    public boolean eliminarInscripcion(int idInscripcion) {
        if (idInscripcion <= 0) {
            throw new IllegalArgumentException("El ID de inscripcion debe ser mayor a cero.");
        }
        return inscripcionDAO.eliminar(idInscripcion);
    }

    public List<String> obtenerComboEstudiantes() {
        return inscripcionDAO.obtenerComboEstudiantes();
    }

    public List<String> obtenerComboGrupos() {
        return inscripcionDAO.obtenerComboGrupos();
    }

    private void validarNotaYEstado(InscripcionCurso inscripcion) {
        Double nota = inscripcion.getNotaFinal();
        if (nota != null) {
            if (nota < 0.0 || nota > 5.0) {
                throw new IllegalArgumentException("La nota final debe estar entre 0.0 y 5.0.");
            }
        }

        String estado = inscripcion.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            estado = "Matriculado";
        } else {
            estado = estado.trim();
        }

        if (!estado.equals("Matriculado") && !estado.equals("Aprobado")
                && !estado.equals("Reprobado") && !estado.equals("Cancelado")) {
            throw new IllegalArgumentException("El estado de matricula no es valido.");
        }

        if (nota != null && !estado.equals("Cancelado")) {
            if (nota >= 3.0) {
                estado = "Aprobado";
            } else {
                estado = "Reprobado";
            }
        }
        inscripcion.setEstado(estado);
    }
}
