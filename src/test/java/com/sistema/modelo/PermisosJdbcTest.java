package com.sistema.modelo;

import com.sistema.servicio.*;
import com.sistema.soporte.BaseAcademicaTemporal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PermisosJdbcTest {
    @Test void guardaYRecuperaVinculosConLosDAOReales() throws Exception {
        try (BaseAcademicaTemporal base = new BaseAcademicaTemporal()) {
            assertTrue(base.conexion.getMetaData().getURL().startsWith("jdbc:h2:mem:"));
            UsuarioService servicio = new UsuarioService();
            Usuario estudiante = new Usuario(0,"ana","clave-prueba","ESTUDIANTE",1,null);
            assertTrue(servicio.registrarUsuario(estudiante));
            Usuario leido = servicio.buscarPorId(estudiante.getIdUsuario());
            assertEquals(1, leido.getIdEstudiante());
            assertNull(leido.getIdDocente());
            assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(
                    new Usuario(0,"duplicada","clave-prueba","ESTUDIANTE",1,null)));
            leido.setRol("DOCENTE");
            leido.setIdEstudiante(null);
            leido.setIdDocente(2);
            assertTrue(servicio.actualizarUsuario(leido));
            leido = servicio.buscarPorId(leido.getIdUsuario());
            assertNull(leido.getIdEstudiante());
            assertEquals(2, leido.getIdDocente());
            assertNotNull(servicio.autenticar("ana","clave-prueba"));
        }
    }

    @Test void sqlFiltraGruposYRechazaCambiosAInscripcionesAjenas() throws Exception {
        try (BaseAcademicaTemporal base = new BaseAcademicaTemporal()) {
            InscripcionCursoService servicio = new InscripcionCursoService();
            Usuario docente = new Usuario(1,"docente","hash","DOCENTE",null,1);
            var propias = servicio.listarParaUsuario(docente);
            assertEquals(1, propias.size());
            assertEquals(1, propias.get(0).getIdGrupo());
            InscripcionCurso propia = propias.get(0);
            propia.setNotaFinal(4.5);
            assertTrue(servicio.actualizarCalificacion(propia, docente));
            InscripcionCurso ajena = new InscripcionCurso();
            ajena.setIdInscripcion(2);
            ajena.setNotaFinal(1.0);
            ajena.setEstado("Reprobado");
            assertThrows(IllegalArgumentException.class, () -> servicio.actualizarCalificacion(ajena, docente));
            try (var s = base.conexion.createStatement(); var r = s.executeQuery("SELECT nota_final FROM inscripcion_curso WHERE id_inscripcion=2")) {
                assertTrue(r.next());
                assertEquals(3.5, r.getDouble(1));
            }
            var propiasEstudiante = servicio.listarParaUsuario(new Usuario(2,"luis","hash","ESTUDIANTE",2,null));
            assertEquals(1, propiasEstudiante.size());
            assertEquals(2, propiasEstudiante.get(0).getIdEstudiante());
        }
    }
}
