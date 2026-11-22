package com.sistema.servicio;

import com.sistema.modelo.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PermisosInscripcionTest {
    private int docenteConsultado;
    private int estudianteConsultado;
    private boolean guardada;
    private final InscripcionCursoDAO dao = new InscripcionCursoDAO() {
        @Override public List<InscripcionCurso> listar() { throw new AssertionError("No debe consultar el listado general"); }
        @Override public List<InscripcionCurso> listarPorDocente(int id) {
            docenteConsultado = id;
            return List.of(new InscripcionCurso(5, 10, null, "Matriculado"));
        }
        @Override public List<InscripcionCurso> listarPorEstudiante(int id) {
            estudianteConsultado = id;
            return List.of();
        }
        @Override public boolean actualizarPorDocente(InscripcionCurso i, int docente) {
            guardada = docente == 7 && i.getIdInscripcion() == 42;
            return guardada;
        }
        @Override public boolean actualizar(InscripcionCurso i) { throw new AssertionError("No debe guardar sin comprobar el docente"); }
        @Override public boolean existeMatricula(int estudiante, int grupo) { return false; }
        @Override public boolean matricular(InscripcionCurso i) { guardada = true; return true; }
        @Override public boolean eliminar(int id) { throw new AssertionError("No debe eliminar"); }
    };
    private final InscripcionCursoService servicio = new InscripcionCursoService(dao);
    private Usuario usuario(String rol, Integer estudiante, Integer docente) {
        return new Usuario(1, "cuenta", "hash", rol, estudiante, docente);
    }
    private InscripcionCurso nota(int id) {
        InscripcionCurso i = new InscripcionCurso();
        i.setIdInscripcion(id);
        i.setNotaFinal(4.0);
        i.setEstado("Matriculado");
        return i;
    }

    @Test void consultaSoloLaIdentidadDeLaSesion() {
        servicio.listarParaUsuario(usuario("DOCENTE", null, 7));
        assertEquals(7, docenteConsultado);
        servicio.listarParaUsuario(usuario("ESTUDIANTE", 5, null));
        assertEquals(5, estudianteConsultado);
    }

    @Test void rechazaSesionAusenteRolesDesconocidosYAsociacionesVacias() {
        assertThrows(IllegalArgumentException.class, () -> servicio.listarParaUsuario(null));
        assertThrows(IllegalArgumentException.class, () -> servicio.listarParaUsuario(usuario("OTRO", null, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.listarParaUsuario(usuario("ESTUDIANTE", null, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.listarParaUsuario(usuario("DOCENTE", null, null)));
    }

    @Test void docenteSoloPuedeGuardarInscripcionesPropias() {
        InscripcionCurso i = nota(42);
        assertTrue(servicio.actualizarCalificacion(i, usuario("DOCENTE", null, 7)));
        assertEquals("Aprobado", i.getEstado());
        guardada = false;
        assertThrows(IllegalArgumentException.class, () -> servicio.actualizarCalificacion(nota(43), usuario("DOCENTE", null, 7)));
        assertFalse(guardada);
        assertThrows(IllegalArgumentException.class, () -> servicio.actualizarCalificacion(nota(42), usuario("DOCENTE", null, 8)));
        assertFalse(guardada);
    }

    @Test void estudianteNoPuedeAsignarNotasNiEliminar() {
        Usuario estudiante = usuario("ESTUDIANTE", 5, null);
        assertThrows(IllegalArgumentException.class, () -> servicio.actualizarCalificacion(nota(42), estudiante));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarInscripcion(42, estudiante));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarInscripcion(42, usuario("DOCENTE", null, 7)));
        assertFalse(guardada);
    }

    @Test void estudianteNoPuedeMatricularAOtraPersonaNiAsignarseNota() {
        Usuario estudiante = usuario("ESTUDIANTE", 5, null);
        assertThrows(IllegalArgumentException.class, () -> servicio.matricularEstudiante(new InscripcionCurso(6, 10, null, "Matriculado"), estudiante));
        assertThrows(IllegalArgumentException.class, () -> servicio.matricularEstudiante(new InscripcionCurso(5, 10, 5.0, "Aprobado"), estudiante));
        assertThrows(IllegalArgumentException.class, () -> servicio.matricularEstudiante(new InscripcionCurso(5, 10, null, "Cancelado"), estudiante));
        assertFalse(guardada);
        assertTrue(servicio.matricularEstudiante(new InscripcionCurso(5, 10, null, "Matriculado"), estudiante));
    }
}
