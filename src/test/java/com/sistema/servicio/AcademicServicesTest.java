package com.sistema.servicio;

import com.sistema.config.SeguridadUtil;
import com.sistema.modelo.Docente;
import com.sistema.modelo.DocenteDAO;
import com.sistema.modelo.Grupo;
import com.sistema.modelo.GrupoDAO;
import com.sistema.modelo.InscripcionCurso;
import com.sistema.modelo.InscripcionCursoDAO;
import com.sistema.modelo.Materia;
import com.sistema.modelo.MateriaDAO;
import com.sistema.modelo.Usuario;
import com.sistema.modelo.UsuarioDAO;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias para los servicios del sistema academico
class AcademicServicesTest {

    @Test
    void testMateriaService() {
        MateriaDAO daoMemoria = new MateriaDAO() {
            @Override
            public boolean insertar(Materia m) {
                m.setIdMateria(1);
                return true;
            }
        };

        MateriaService service = new MateriaService(daoMemoria);
        Materia materia = new Materia("Programacion II", 4);
        assertTrue(service.registrarMateria(materia));
        assertEquals(1, materia.getIdMateria());
    }

    @Test
    void testDocenteService() {
        DocenteDAO daoMemoria = new DocenteDAO() {
            @Override
            public boolean insertar(Docente d) {
                d.setIdDocente(1);
                return true;
            }
        };

        DocenteService service = new DocenteService(daoMemoria);
        Docente docente = new Docente("Carlos Gomez", "Ingenieria de Software");
        assertTrue(service.registrarDocente(docente));
        assertEquals(1, docente.getIdDocente());
    }

    @Test
    void testGrupoService() throws SQLException {
        GrupoDAO daoMemoria = new GrupoDAO() {
            @Override
            public int insertar(Grupo g) {
                g.setIdGrupo(1);
                return 1;
            }
        };

        GrupoService service = new GrupoService(daoMemoria);
        int id = service.crearGrupo(1, 1, "Lab 301", "Lunes 8-10");
        assertEquals(1, id);
    }

    @Test
    void testInscripcionCursoService() {
        InscripcionCursoDAO daoMemoria = new InscripcionCursoDAO() {
            private final List<InscripcionCurso> lista = new ArrayList<>();

            @Override
            public boolean existeMatricula(int idEst, int idGrp) {
                for (InscripcionCurso i : lista) {
                    if (i.getIdEstudiante() == idEst && i.getIdGrupo() == idGrp) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean matricular(InscripcionCurso i) {
                i.setIdInscripcion(lista.size() + 1);
                lista.add(i);
                return true;
            }
        };

        InscripcionCursoService service = new InscripcionCursoService(daoMemoria);

        // Nota mayor o igual a 3.0 debe quedar en estado Aprobado
        InscripcionCurso insc1 = new InscripcionCurso(1, 1, 4.0, "Matriculado");
        assertTrue(service.matricularEstudiante(insc1));
        assertEquals("Aprobado", insc1.getEstado());

        // Nota menor a 3.0 debe quedar en estado Reprobado
        InscripcionCurso insc2 = new InscripcionCurso(2, 1, 2.5, "Matriculado");
        assertTrue(service.matricularEstudiante(insc2));
        assertEquals("Reprobado", insc2.getEstado());
    }

    @Test
    void testUsuarioService() {
        UsuarioDAO daoMemoria = new UsuarioDAO() {
            private final List<Usuario> usuarios = new ArrayList<>();

            @Override
            public Usuario buscarPorUsername(String username) {
                for (Usuario u : usuarios) {
                    if (u.getUsername().equalsIgnoreCase(username)) {
                        return u;
                    }
                }
                return null;
            }

            @Override
            public boolean insertar(Usuario u) {
                u.setIdUsuario(1);
                usuarios.add(u);
                return true;
            }
        };

        UsuarioService service = new UsuarioService(daoMemoria);

        Usuario nuevo = new Usuario("admin_prueba", "admin123", "ADMIN");
        assertTrue(service.registrarUsuario(nuevo));
        assertEquals(SeguridadUtil.hashSHA512("admin123"), nuevo.getPassword());

        Usuario loginOk = service.autenticar("admin_prueba", "admin123");
        assertNotNull(loginOk);
        assertEquals("ADMIN", loginOk.getRol());
    }
}
