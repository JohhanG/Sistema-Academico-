package com.sistema.servicio;

import com.sistema.config.*;
import com.sistema.modelo.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReglasEntregaTest {
    private final InscripcionCursoService matriculas = new InscripcionCursoService(new InscripcionCursoDAO() {
        @Override public boolean existeMatricula(int estudiante,int grupo) { return false; }
        @Override public boolean matricular(InscripcionCurso i) { return true; }
        @Override public boolean actualizarPorDocente(InscripcionCurso i,int docente) { return true; }
    });
    @Test void rechazaNotasNoFinitasYEstadosSinNota() {
        for(double nota:new double[]{Double.NaN,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY,-0.1,5.1})
            assertThrows(IllegalArgumentException.class,()->matriculas.matricularEstudiante(new InscripcionCurso(1,1,nota,"Matriculado")));
        for(String estado:new String[]{"Aprobado","Reprobado"})
            assertThrows(IllegalArgumentException.class,()->matriculas.matricularEstudiante(new InscripcionCurso(1,1,null,estado)));
        InscripcionCurso limite=new InscripcionCurso(1,1,2.95,"Matriculado");
        assertTrue(matriculas.matricularEstudiante(limite));
        assertEquals(3.0,limite.getNotaFinal()); assertEquals("Aprobado",limite.getEstado());
    }
    @Test void docenteNoPuedeCancelarMedianteCalificacion() {
        InscripcionCurso i=new InscripcionCurso(1,1,null,"Cancelado"); i.setIdInscripcion(1);
        Usuario docente=new Usuario(2,"docente","clave","DOCENTE",null,1);
        assertThrows(IllegalArgumentException.class,()->matriculas.actualizarCalificacion(i,docente));
    }
    @Test void validaCorreoYLimitesSinConsultarLaBase() {
        assertEquals("ana@example.com",Validaciones.correo(" ANA@EXAMPLE.COM "));
        for(String correo:new String[]{"a@b","a@@b.com","ana lopez@example.com","@example.com"})
            assertThrows(IllegalArgumentException.class,()->Validaciones.correo(correo));
        MateriaService materias=new MateriaService(new MateriaDAO(){ @Override public boolean insertar(Materia m){return true;} });
        for(int creditos:new int[]{0,11})assertThrows(IllegalArgumentException.class,()->materias.registrarMateria(new Materia("Materia",creditos)));
        assertTrue(materias.registrarMateria(new Materia("Materia",10)));
    }
    @Test void erroresConservanLaCausaSinExponerDetallesYClavesMalformadasNoAutentican() {
        for(String estado:new String[]{"08001","23505","23503","23514"}) {
            SQLException causa=new SQLException("dato-confidencial",estado);
            ErrorDatos error=ErrorDatos.desde(causa);
            assertSame(causa,error.getCause());assertFalse(error.getMessage().contains("dato-confidencial"));
        }
        for(String hash:new String[]{"clave-plana","pbkdf2$999999999$invalid$invalid","pbkdf2$600000$!$!"})
            assertFalse(SeguridadUtil.verificarPassword("clave-plana",hash));
        String primero=SeguridadUtil.hashPassword("clave-segura");
        String segundo=SeguridadUtil.hashPassword("clave-segura");
        assertNotEquals(primero,segundo);assertTrue(SeguridadUtil.verificarPassword("clave-segura",primero));
        assertFalse(SeguridadUtil.verificarPassword("otra-clave",primero));
    }
}
