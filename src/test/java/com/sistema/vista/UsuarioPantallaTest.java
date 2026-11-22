package com.sistema.vista;

import com.sistema.controlador.UsuarioController;
import com.sistema.servicio.UsuarioService;
import com.sistema.soporte.BaseAcademicaTemporal;
import org.junit.jupiter.api.Test;
import javax.swing.SwingUtilities;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioPantallaTest {
    private static class VistaPrueba extends UsuarioView {
        String error;
        @Override public void mostrarMensaje(String mensaje) { }
        @Override public void mostrarError(String mensaje) { error = mensaje; }
    }

    @Test void altaEdicionCambioDeRolYLimpiezaDesdeElFormulario() throws Exception {
        try (BaseAcademicaTemporal base = new BaseAcademicaTemporal()) {
            SwingUtilities.invokeAndWait(() -> {
                VistaPrueba vista = new VistaPrueba();
                try {
                    new UsuarioController(vista, new UsuarioService()).iniciar();
                    assertFalse(vista.getCmbEstudiante().isEnabled());
                    assertFalse(vista.getCmbDocente().isEnabled());
                    vista.getCmbRol().setSelectedItem("ESTUDIANTE");
                    assertTrue(vista.getCmbEstudiante().isEnabled());
                    assertFalse(vista.getCmbDocente().isEnabled());
                    vista.getTxtUsername().setText("cuenta-prueba");
                    vista.getTxtPassword().setText("clave-prueba");
                    vista.getBtnGuardar().doClick();
                    assertNotNull(vista.error, "Debe exigir seleccion de estudiante");
                    assertEquals(0, vista.getModeloTabla().getRowCount());
                    vista.error = null;
                    vista.getCmbEstudiante().setSelectedIndex(0);
                    vista.getBtnGuardar().doClick();
                    assertNull(vista.error);
                    assertEquals(1, vista.getModeloTabla().getRowCount());
                    assertEquals(1, vista.getModeloTabla().getValueAt(0, 3));
                    vista.getTblUsuarios().setRowSelectionInterval(0, 0);
                    assertTrue(vista.getCmbEstudiante().getSelectedItem().toString().startsWith("1 - "));
                    assertEquals(0, vista.getTxtPassword().getPassword().length);
                    vista.getCmbRol().setSelectedItem("DOCENTE");
                    assertEquals(-1, vista.getCmbEstudiante().getSelectedIndex());
                    assertTrue(vista.getCmbDocente().isEnabled());
                    vista.getCmbDocente().setSelectedIndex(1);
                    vista.getBtnActualizar().doClick();
                    assertNull(vista.error);
                    assertEquals("DOCENTE", vista.getModeloTabla().getValueAt(0,2));
                    assertNull(vista.getModeloTabla().getValueAt(0,3));
                    assertEquals(2, vista.getModeloTabla().getValueAt(0,4));
                    assertNotNull(new UsuarioService().autenticar("cuenta-prueba", "clave-prueba"));
                    vista.getTblUsuarios().setRowSelectionInterval(0,0);
                    assertTrue(vista.getCmbDocente().getSelectedItem().toString().startsWith("2 - "));
                    vista.getBtnLimpiar().doClick();
                    assertEquals("ADMIN", vista.getCmbRol().getSelectedItem());
                    assertEquals(-1, vista.getCmbDocente().getSelectedIndex());
                    assertEquals(-1, vista.getCmbEstudiante().getSelectedIndex());
                    assertFalse(vista.getCmbDocente().isEnabled());
                } finally { vista.dispose(); }
            });
        }
    }
}
