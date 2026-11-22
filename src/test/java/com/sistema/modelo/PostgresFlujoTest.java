package com.sistema.modelo;

import com.sistema.config.*;
import com.sistema.servicio.*;
import com.sistema.vista.ConsolaAcademica;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import java.nio.file.*;
import java.sql.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Prueba optativa contra una instancia PostgreSQL local y descartable. Nunca usa Supabase. */
@EnabledIfSystemProperty(named="sistema.test.pg",matches="true")
class PostgresFlujoTest {
    @Test void instalacionRepetibleYRecorridoAcademicoConPostgreSQLReal() throws Exception {
        String url=System.getProperty("sistema.test.pg.url","jdbc:postgresql://127.0.0.1:55439/postgres");
        if (!url.equals("jdbc:postgresql://127.0.0.1:55439/postgres")) throw new IllegalArgumentException("Solo se permite la instancia temporal local de pruebas.");
        Field campo=ConexionBD.class.getDeclaredField("conexion");campo.setAccessible(true);
        Connection anterior=(Connection)campo.get(null);
        String schema="prueba_"+UUID.randomUUID().toString().replace("-","");
        try(Connection c=DriverManager.getConnection(url,"postgres",""); Statement s=c.createStatement()) {
            s.execute("CREATE SCHEMA "+schema);
            s.execute("SET search_path TO "+schema);
            campo.set(null,c);
            try {
                for(int vez=0;vez<2;vez++) {
                    s.execute(Files.readString(Path.of("schema.sql")));
                    s.execute(Files.readString(Path.of("sql/002_proteger_historial.sql")));
                    s.execute(Files.readString(Path.of("sql/datos_demostracion.sql")));
                }
                assertEquals(2,new MateriaDAO().listar().size());
                assertEquals(2,new DocenteDAO().listar().size());
                assertEquals(1,new GrupoDAO().listarTodos().size());
                assertEquals(2,new EstudianteDao().obtenerTodos().size());
                UsuarioService cuentas=new UsuarioService();
                assertNotNull(cuentas.autenticar("admin","admin123"));
                // Repetir las semillas no debe cambiar la contrasena de una cuenta existente.
                Usuario admin=cuentas.autenticar("admin","admin123");
                admin.setPassword("otra-clave-segura");assertTrue(cuentas.actualizarUsuario(admin));
                s.execute(Files.readString(Path.of("sql/datos_demostracion.sql")));
                assertNull(cuentas.autenticar("admin","admin123"));
                assertNotNull(cuentas.autenticar("admin","otra-clave-segura"));
                admin.setPassword("0123456789abcdef0123456789abcdef");
                assertTrue(cuentas.actualizarUsuario(admin));
                assertTrue(admin.getPassword().startsWith("pbkdf2$"));
                assertNotNull(cuentas.autenticar("admin","0123456789abcdef0123456789abcdef"));
                String hashAnterior=admin.getPassword();
                assertTrue(cuentas.actualizarUsuario(admin));
                assertEquals(hashAnterior,admin.getPassword());

                EstudianteService estudiantes=new EstudianteService();
                Estudiante e=estudiantes.registrarEstudiante("Nuevo","Estudiante"," NUEVO@EXAMPLE.TEST ");
                assertEquals("nuevo@example.test",e.getEmail());
                assertThrows(IllegalArgumentException.class,()->estudiantes.registrarEstudiante("Otro","Estudiante","nuevo@example.test"));
                Docente d=new Docente("Nuevo docente","Software");assertTrue(new DocenteService().registrarDocente(d));
                Materia m=new Materia("Materia prueba",4);assertTrue(new MateriaService().registrarMateria(m));
                int idGrupo=new GrupoService().crearGrupo(m.getIdMateria(),d.getIdDocente(),"B2","Viernes");
                Usuario alumno=new Usuario(0,"alumno-prueba","clave-prueba","ESTUDIANTE",e.getIdEstudiante(),null);
                Usuario profesor=new Usuario(0,"docente-prueba","clave-prueba","DOCENTE",null,d.getIdDocente());
                assertTrue(cuentas.registrarUsuario(alumno));assertTrue(cuentas.registrarUsuario(profesor));
                assertNotNull(cuentas.autenticar("alumno-prueba","clave-prueba"));
                InscripcionCursoService servicio=new InscripcionCursoService();
                InscripcionCurso i=new InscripcionCurso(e.getIdEstudiante(),idGrupo,null,"Matriculado");
                assertTrue(servicio.matricularEstudiante(i,alumno));
                assertThrows(IllegalArgumentException.class,()->servicio.matricularEstudiante(i,alumno));
                assertEquals(1,servicio.listarParaUsuario(alumno).size());
                i.setNotaFinal(2.96);assertTrue(servicio.actualizarCalificacion(i,profesor));
                assertEquals(3.0,i.getNotaFinal());assertEquals("Aprobado",i.getEstado());
                assertEquals(3.0,servicio.listarParaUsuario(alumno).get(0).getNotaFinal());
                Usuario ajeno=new Usuario(99,"ajeno","hash","DOCENTE",null,1);
                assertThrows(IllegalArgumentException.class,()->servicio.actualizarCalificacion(i,ajeno));
                assertThrows(IllegalArgumentException.class,()->new MateriaService().eliminarMateria(m.getIdMateria()));
                assertThrows(IllegalArgumentException.class,()->new GrupoService().eliminarGrupo(idGrupo));
                assertThrows(IllegalArgumentException.class,()->new DocenteService().eliminarDocente(d.getIdDocente()));
                assertThrows(IllegalArgumentException.class,()->estudiantes.eliminarEstudiante(e.getIdEstudiante()));
                assertTrue(servicio.eliminarInscripcion(i.getIdInscripcion(),admin));
                InscripcionCurso cancelada=servicio.listarParaUsuario(alumno).get(0);
                assertEquals("Cancelado",cancelada.getEstado());assertEquals(3.0,cancelada.getNotaFinal());
                assertFalse(servicio.eliminarInscripcion(i.getIdInscripcion(),admin));
                assertThrows(IllegalArgumentException.class,()->servicio.actualizarCalificacion(i,profesor));
                assertEquals(1,new BoletinService().cargar(alumno).size());
                // Consola comparte la misma autenticacion, datos y restricciones.
                var texto=new java.io.ByteArrayOutputStream();
                new ConsolaAcademica(new Scanner("alumno-prueba\nclave-prueba\n1\n5\n0\n"),new java.io.PrintStream(texto)).iniciar();
                String salida=texto.toString(java.nio.charset.StandardCharsets.UTF_8);
                assertTrue(salida.contains("Cancelado"));assertTrue(salida.contains("Se requiere rol administrador"));
                // Persistencia despues de volver a conectar al mismo esquema temporal.
                try(Connection segunda=DriverManager.getConnection(url,"postgres","");Statement lectura=segunda.createStatement()) {
                    lectura.execute("SET search_path TO "+schema);
                    try(ResultSet r=lectura.executeQuery("SELECT estado,nota_final FROM inscripcion_curso WHERE id_inscripcion="+i.getIdInscripcion())) {
                        assertTrue(r.next());assertEquals("Cancelado",r.getString(1));assertEquals(3.0,r.getDouble(2));
                    }
                }
            } finally {
                campo.set(null,anterior);
                s.execute("SET search_path TO public");s.execute("DROP SCHEMA "+schema+" CASCADE");
            }
        }
    }
}
