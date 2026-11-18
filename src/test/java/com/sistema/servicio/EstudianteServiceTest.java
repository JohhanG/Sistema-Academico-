package com.sistema.servicio;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias para EstudianteService
class EstudianteServiceTest {

    private EstudianteService service;

    // DAO en memoria para probar sin tocar la base de datos
    static class EstudianteDaoMemoria extends EstudianteDao {
        private final List<Estudiante> lista = new ArrayList<>();
        private int contadorId = 1;

        @Override
        public void guardar(Estudiante estudiante) {
            estudiante.setIdEstudiante(contadorId++);
            lista.add(new Estudiante(
                    estudiante.getIdEstudiante(),
                    estudiante.getNombre(),
                    estudiante.getApellido(),
                    estudiante.getEmail()
            ));
        }

        @Override
        public List<Estudiante> obtenerTodos() {
            return new ArrayList<>(lista);
        }

        @Override
        public Optional<Estudiante> buscarPorId(int id) {
            for (Estudiante e : lista) {
                if (e.getIdEstudiante() == id) {
                    return Optional.of(e);
                }
            }
            return Optional.empty();
        }

        @Override
        public Optional<Estudiante> buscarPorEmail(String email) {
            for (Estudiante e : lista) {
                if (e.getEmail().equalsIgnoreCase(email)) {
                    return Optional.of(e);
                }
            }
            return Optional.empty();
        }

        @Override
        public void actualizar(Estudiante estudiante) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getIdEstudiante() == estudiante.getIdEstudiante()) {
                    lista.set(i, estudiante);
                    return;
                }
            }
        }

        @Override
        public void eliminar(int id) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getIdEstudiante() == id) {
                    lista.remove(i);
                    return;
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        service = new EstudianteService(new EstudianteDaoMemoria());
    }

    @Test
    void testRegistrarEstudiante() {
        Estudiante creado = service.registrarEstudiante("Santiago", "Palma", "santiago@uniajc.edu.co");

        assertNotNull(creado);
        assertEquals(1, creado.getIdEstudiante());
        assertEquals(1, service.obtenerTodosLosEstudiantes().size());
    }

    @Test
    void testEmailDuplicado() {
        service.registrarEstudiante("Santiago", "Palma", "santiago@uniajc.edu.co");

        try {
            service.registrarEstudiante("Otro", "Apellido", "santiago@uniajc.edu.co");
            fail("Debio lanzar excepcion por email duplicado");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void testCamposInvalidos() {
        try {
            service.registrarEstudiante("", "Palma", "santiago@uniajc.edu.co");
            fail("Debio fallar por nombre vacio");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            service.registrarEstudiante("Santiago", "Palma", "correo-sin-arroba");
            fail("Debio fallar por email invalido");
        } catch (IllegalArgumentException ignored) {
        }
    }
}
