package com.sistema.servicio;

import com.sistema.modelo.Docente;
import com.sistema.modelo.DocenteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias para DocenteService
class DocenteServiceTest {

    private DocenteService service;

    // DAO en memoria para probar sin tocar la base de datos
    static class DocenteDaoMemoria extends DocenteDAO {
        private final List<Docente> lista = new ArrayList<>();
        private int contadorId = 1;

        @Override
        public boolean insertar(Docente docente) {
            docente.setIdDocente(contadorId++);
            lista.add(new Docente(
                    docente.getIdDocente(),
                    docente.getNombre(),
                    docente.getEspecialidad()
            ));
            return true;
        }

        @Override
        public List<Docente> listar() {
            return new ArrayList<>(lista);
        }

        @Override
        public Docente buscarPorId(int idDocente) {
            for (Docente d : lista) {
                if (d.getIdDocente() == idDocente) {
                    return d;
                }
            }
            return null;
        }

        @Override
        public boolean actualizar(Docente docente) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getIdDocente() == docente.getIdDocente()) {
                    lista.set(i, docente);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean eliminar(int idDocente) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getIdDocente() == idDocente) {
                    lista.remove(i);
                    return true;
                }
            }
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        service = new DocenteService(new DocenteDaoMemoria());
    }

    @Test
    void testRegistrarDocente() {
        Docente nuevo = new Docente("Carlos Gomez", "Ingenieria de Software");
        boolean registrado = service.registrarDocente(nuevo);

        assertTrue(registrado);
        assertEquals(1, nuevo.getIdDocente());
        assertEquals(1, service.listarDocentes().size());
    }

    @Test
    void testCamposInvalidos() {
        try {
            service.registrarDocente(new Docente("", "Bases de Datos"));
            fail("Debio fallar por nombre vacio");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            service.registrarDocente(new Docente("Marta Rodriguez", ""));
            fail("Debio fallar por especialidad vacia");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    void testActualizarYEliminarDocente() {
        Docente doc = new Docente("Marta Rodriguez", "Bases de Datos");
        service.registrarDocente(doc);

        Docente modificado = new Docente(doc.getIdDocente(), "Marta Rodriguez", "Arquitectura de Datos");
        assertTrue(service.actualizarDocente(modificado));
        assertEquals("Arquitectura de Datos", service.buscarDocentePorId(doc.getIdDocente()).getEspecialidad());

        assertTrue(service.eliminarDocente(doc.getIdDocente()));
        assertTrue(service.listarDocentes().isEmpty());
    }
}
