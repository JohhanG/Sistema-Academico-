package com.sistema.vista;

import com.sistema.controlador.InscripcionCursoController;
import com.sistema.modelo.Usuario;
import com.sistema.servicio.InscripcionCursoService;
import com.sistema.soporte.BaseAcademicaTemporal;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class PermisosPantallaTest {
    private static class MatriculasPrueba extends InscripcionCursoView {
        String error;
        @Override public void mostrarError(String mensaje) { error = mensaje; }
        @Override public void mostrarMensaje(String mensaje) { }
    }

    @Test void docenteVeSoloSusGruposEnLaTablaYEnLosSelectores() throws Exception {
        try (BaseAcademicaTemporal base = new BaseAcademicaTemporal()) {
            SwingUtilities.invokeAndWait(() -> {
                MatriculasPrueba vista = new MatriculasPrueba();
                try {
                    Usuario docente = new Usuario(1,"docente","hash","DOCENTE",null,1);
                    new InscripcionCursoController(vista,new InscripcionCursoService(),docente).iniciar();
                    assertNull(vista.error);
                    assertEquals(1,vista.getModeloTabla().getRowCount());
                    assertEquals(1,vista.getModeloTabla().getValueAt(0,3));
                    assertEquals(1,vista.getCmbEstudiante().getItemCount());
                    assertEquals(1,vista.getCmbGrupo().getItemCount());
                    assertFalse(vista.getBtnMatricular().isVisible());
                    assertFalse(vista.getBtnEliminar().isVisible());
                    vista.getTblInscripciones().setRowSelectionInterval(0,0);
                    vista.getTxtNotaFinal().setText("4,5");
                    vista.getBtnActualizar().doClick();
                    assertNull(vista.error);
                    assertEquals(4.5,vista.getModeloTabla().getValueAt(0,5));
                } finally { vista.dispose(); }
            });
        }
    }

    @Test void cuentaSinVinculoNoCargaDatosAjenos() throws Exception {
        try (BaseAcademicaTemporal base = new BaseAcademicaTemporal()) {
            SwingUtilities.invokeAndWait(() -> {
                MatriculasPrueba vista = new MatriculasPrueba();
                try {
                    new InscripcionCursoController(vista,new InscripcionCursoService(),
                            new Usuario(1,"Ana","hash","ESTUDIANTE",null,null)).iniciar();
                    assertNotNull(vista.error);
                    assertEquals(0,vista.getModeloTabla().getRowCount());
                    assertEquals(0,vista.getCmbEstudiante().getItemCount());
                    assertFalse(vista.getBtnMatricular().isEnabled());
                    vista.getBtnRefrescar().doClick();
                    assertEquals(0,vista.getModeloTabla().getRowCount());
                } finally { vista.dispose(); }
            });
        }
    }

    @Test void boletinDelDocenteNoIncluyeMateriasDeOtroDocenteYLimpiaDatosAlRefrescar() throws Exception {
        try (BaseAcademicaTemporal base = new BaseAcademicaTemporal()) {
            SwingUtilities.invokeAndWait(() -> {
                ConsultaNotasView vista = new ConsultaNotasView(new Usuario(1,"docente","hash","DOCENTE",null,1),null);
                try {
                    JTable tabla = campo(vista,"tblNotas",JTable.class);
                    JComboBox<?> estudiantes = campo(vista,"cmbEstudiantes",JComboBox.class);
                    assertEquals(1,estudiantes.getItemCount());
                    assertEquals(1,tabla.getRowCount());
                    assertEquals("PROGRAMACION",tabla.getValueAt(0,1));
                    try (var s = base.conexion.createStatement()) { s.executeUpdate("UPDATE grupo SET id_docente=2 WHERE id_grupo=1"); }
                    campo(vista,"btnRefrescar",JButton.class).doClick();
                    assertEquals(0,tabla.getRowCount());
                    assertEquals(0,estudiantes.getItemCount());
                    assertEquals("Sin datos disponibles",campo(vista,"lblValNombre",JLabel.class).getText());
                } catch (Exception e) { throw new RuntimeException(e); }
                finally { vista.dispose(); }
            });
        }
    }

    private static <T> T campo(Object objeto,String nombre,Class<T> tipo) {
        try {
            Field f=objeto.getClass().getDeclaredField(nombre);
            f.setAccessible(true);
            return tipo.cast(f.get(objeto));
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }
}
