package com.sistema.servicio;

import com.sistema.modelo.InscripcionCurso;
import com.sistema.modelo.InscripcionCursoDAO;
import com.sistema.modelo.Usuario;

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

    public List<InscripcionCurso> listarParaUsuario(Usuario usuario) {
        String rol = exigirRol(usuario);
        if (rol.equals("ADMIN")) return listarInscripciones();
        if (rol.equals("ESTUDIANTE")) {
            if (usuario.getIdEstudiante() == null || usuario.getIdEstudiante() <= 0) {
                throw new IllegalArgumentException("La cuenta no tiene estudiante asociado.");
            }
            return listarPorEstudiante(usuario.getIdEstudiante());
        }
        if (usuario.getIdDocente() == null || usuario.getIdDocente() <= 0) {
            throw new IllegalArgumentException("La cuenta no tiene docente asociado.");
        }
        return inscripcionDAO.listarPorDocente(usuario.getIdDocente());
    }

    public boolean actualizarCalificacion(InscripcionCurso inscripcion, Usuario usuario) {
        String rol = exigirRol(usuario);
        if (rol.equals("ADMIN")) return actualizarCalificacion(inscripcion);
        if (!rol.equals("DOCENTE") || usuario.getIdDocente() == null || usuario.getIdDocente() <= 0) {
            throw new IllegalArgumentException("Solo un docente vinculado puede calificar sus grupos.");
        }
        if (inscripcion == null || inscripcion.getIdInscripcion() <= 0) {
            throw new IllegalArgumentException("El ID de inscripcion no es valido.");
        }
        validarNotaYEstado(inscripcion);
        // La pertenencia se comprueba en el mismo UPDATE para evitar cambios de grupo entre consulta y guardado.
        if (!inscripcionDAO.actualizarPorDocente(inscripcion, usuario.getIdDocente())) {
            throw new IllegalArgumentException("No se pudo calificar. Verifica que la inscripcion pertenece a uno de tus grupos y que existe conexion.");
        }
        return true;
    }

    public boolean matricularEstudiante(InscripcionCurso inscripcion, Usuario usuario) {
        String rol = exigirRol(usuario);
        if (rol.equals("ADMIN")) return matricularEstudiante(inscripcion);
        if (!rol.equals("ESTUDIANTE") || usuario.getIdEstudiante() == null
                || inscripcion == null || usuario.getIdEstudiante() != inscripcion.getIdEstudiante()) {
            throw new IllegalArgumentException("Solo puedes matricular al estudiante asociado a tu cuenta.");
        }
        if (inscripcion.getNotaFinal() != null || !"Matriculado".equals(inscripcion.getEstado())) {
            throw new IllegalArgumentException("El estudiante no puede asignar notas ni estados de calificacion.");
        }
        return matricularEstudiante(inscripcion);
    }

    public boolean eliminarInscripcion(int idInscripcion, Usuario usuario) {
        if (!exigirRol(usuario).equals("ADMIN")) {
            throw new IllegalArgumentException("Solo el administrador puede eliminar inscripciones.");
        }
        return eliminarInscripcion(idInscripcion);
    }

    private String exigirRol(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            throw new IllegalArgumentException("Se requiere una sesion valida.");
        }
        String rol = usuario.getRol().trim().toUpperCase(java.util.Locale.ROOT);
        if (!rol.equals("ADMIN") && !rol.equals("DOCENTE") && !rol.equals("ESTUDIANTE")) {
            throw new IllegalArgumentException("El rol no tiene acceso a las inscripciones.");
        }
        return rol;
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
