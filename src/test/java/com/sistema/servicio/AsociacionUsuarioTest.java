package com.sistema.servicio;

import com.sistema.modelo.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class AsociacionUsuarioTest {
    private final UsuarioDAO cuentas = new UsuarioDAO() {
        @Override public Usuario buscarPorId(int id) { return null; }
        @Override public Usuario buscarPorUsername(String username) { return null; }
        @Override public boolean insertar(Usuario u) { return true; }
        @Override public boolean actualizar(Usuario u) { return true; }
        @Override public boolean existeVinculoEnOtraCuenta(Integer estudiante, Integer docente, int cuenta) {
            return (Integer.valueOf(2).equals(estudiante) || Integer.valueOf(2).equals(docente)) && cuenta != 9;
        }
    };
    private final EstudianteDao estudiantes = new EstudianteDao() {
        @Override public Optional<Estudiante> buscarPorId(int id) {
            return id == 1 || id == 2 ? Optional.of(new Estudiante(id, "Ana", "Perez", "ana@example.com")) : Optional.empty();
        }
        @Override public java.util.List<Estudiante> obtenerTodos() {
            throw new AssertionError("La identidad no debe buscarse por nombres ni por el primer estudiante");
        }
    };
    private final DocenteDAO docentes = new DocenteDAO() {
        @Override public Docente buscarPorId(int id) {
            return id == 1 || id == 2 ? new Docente(id, "Docente", "Programacion") : null;
        }
    };
    private final UsuarioService servicio = new UsuarioService(cuentas, estudiantes, docentes);

    private Usuario usuario(String rol, Integer estudiante, Integer docente) {
        return new Usuario(0, "ana", "clave123", rol, estudiante, docente);
    }

    @Test void exigeAsociacionSegunRol() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("ESTUDIANTE", null, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("DOCENTE", null, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("ADMIN", 1, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("ESTUDIANTE", 1, 1)));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("DOCENTE", 1, 1)));
    }

    @Test void rechazaRegistrosInexistentesYNegativos() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("ESTUDIANTE", 88, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("DOCENTE", null, 88)));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("ESTUDIANTE", -1, null)));
    }

    @Test void permiteVinculosValidosYAdminSinVinculo() {
        assertTrue(servicio.registrarUsuario(usuario("ESTUDIANTE", 1, null)));
        assertTrue(servicio.registrarUsuario(usuario("DOCENTE", null, 1)));
        assertTrue(servicio.registrarUsuario(usuario("ADMIN", null, null)));
    }

    @Test void rechazaVinculoYaUtilizado() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("ESTUDIANTE", 2, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarUsuario(usuario("DOCENTE", null, 2)));
    }

    @Test void permiteEditarLaPropiaAsociacionYValidaCambiosDeRol() {
        Usuario u = usuario("ESTUDIANTE", 2, null);
        u.setIdUsuario(9);
        assertTrue(servicio.actualizarUsuario(u));
        u.setRol("DOCENTE");
        assertThrows(IllegalArgumentException.class, () -> servicio.actualizarUsuario(u));
        u.setIdEstudiante(null);
        u.setIdDocente(1);
        assertTrue(servicio.actualizarUsuario(u));
    }

    @Test void cuentaSinVinculoNoAsumeIdentidadPorNombre() {
        IdentidadAcademicaService identidad = new IdentidadAcademicaService(estudiantes);
        assertThrows(IllegalArgumentException.class, () -> identidad.exigirEstudiante(usuario("ESTUDIANTE", null, null)));
        assertThrows(IllegalArgumentException.class, () -> identidad.exigirEstudiante(usuario("DOCENTE", 1, null)));
        assertThrows(IllegalArgumentException.class, () -> identidad.exigirEstudiante(usuario("ESTUDIANTE", 88, null)));
        assertEquals(1, identidad.exigirEstudiante(usuario("ESTUDIANTE", 1, null)).getIdEstudiante());
    }
}
