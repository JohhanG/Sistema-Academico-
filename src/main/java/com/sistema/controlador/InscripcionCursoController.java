package com.sistema.controlador;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;
import com.sistema.modelo.Grupo;
import com.sistema.modelo.GrupoDAO;
import com.sistema.modelo.InscripcionCurso;
import com.sistema.modelo.InscripcionCursoDAO;
import com.sistema.vista.InscripcionCursoView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controlador para la gestión de Matrículas y Calificaciones en el patrón MVC.
 * Coordina la vista con las operaciones DAO de InscripcionCurso, Estudiante y Grupo.
 */
public class InscripcionCursoController implements ActionListener {

    private final InscripcionCursoView vista;
    private final InscripcionCursoDAO modeloDAO;
    private final EstudianteDao estudianteDAO;
    private final GrupoDAO grupoDAO;

    public InscripcionCursoController(InscripcionCursoView vista, InscripcionCursoDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        this.estudianteDAO = new EstudianteDao();
        this.grupoDAO = new GrupoDAO();
        inicializarEventos();
    }

    private void inicializarEventos() {
        this.vista.getBtnMatricular().addActionListener(this);
        this.vista.getBtnActualizar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnRefrescar().addActionListener(this);

        this.vista.getTblInscripciones().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarInscripcionSeleccionada();
                }
            }
        });
    }

    public void iniciar() {
        cargarCombos();
        listarInscripciones();
        vista.setVisible(true);
    }

    /**
     * Llena los combos de estudiantes y grupos con datos de la base de datos.
     */
    public void cargarCombos() {
        try {
            vista.getCmbEstudiante().removeAllItems();
            List<Estudiante> estudiantes = estudianteDAO.obtenerTodos();
            for (Estudiante est : estudiantes) {
                vista.getCmbEstudiante().addItem(est.getIdEstudiante() + " - " + est.getNombre() + " " + est.getApellido());
            }

            vista.getCmbGrupo().removeAllItems();
            List<Grupo> grupos = grupoDAO.listarTodos();
            for (Grupo g : grupos) {
                vista.getCmbGrupo().addItem(g.getIdGrupo() + " - Aula: " + g.getAula() + " (" + g.getHorario() + ")");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar combos de matrícula: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == vista.getBtnMatricular()) {
            matricularEstudiante();
        } else if (source == vista.getBtnActualizar()) {
            actualizarInscripcion();
        } else if (source == vista.getBtnEliminar()) {
            eliminarInscripcion();
        } else if (source == vista.getBtnLimpiar()) {
            vista.limpiarFormulario();
        } else if (source == vista.getBtnRefrescar()) {
            cargarCombos();
            listarInscripciones();
            vista.mostrarMensaje("Listado actualizado correctamente.");
        }
    }

    public void listarInscripciones() {
        DefaultTableModel modelTabla = vista.getModeloTabla();
        modelTabla.setRowCount(0);

        List<InscripcionCurso> lista = modeloDAO.listar();
        for (InscripcionCurso item : lista) {
            Object[] fila = {
                    item.getIdInscripcion(),
                    item.getIdEstudiante(),
                    item.getNombreEstudiante() != null ? item.getNombreEstudiante() : "Estudiante #" + item.getIdEstudiante(),
                    item.getIdGrupo(),
                    item.getInfoGrupo() != null ? item.getInfoGrupo() : "Grupo #" + item.getIdGrupo(),
                    item.getNotaFinal() != null ? item.getNotaFinal() : "Sin Calificar",
                    item.getEstado()
            };
            modelTabla.addRow(fila);
        }
    }

    private void matricularEstudiante() {
        String estSeleccionado = (String) vista.getCmbEstudiante().getSelectedItem();
        String grupoSeleccionado = (String) vista.getCmbGrupo().getSelectedItem();

        if (estSeleccionado == null || grupoSeleccionado == null) {
            vista.mostrarError("Debes seleccionar un Estudiante y un Grupo.");
            return;
        }

        int idEstudiante = extraerIdDeCombo(estSeleccionado);
        int idGrupo = extraerIdDeCombo(grupoSeleccionado);

        if (modeloDAO.existeMatricula(idEstudiante, idGrupo)) {
            vista.mostrarError("El estudiante ya se encuentra matriculado en este grupo.");
            return;
        }

        Double nota = parsearNota(vista.getTxtNotaFinal().getText().trim());
        String estado = (String) vista.getCmbEstado().getSelectedItem();

        if (nota != null) {
            if (nota < 0.0 || nota > 5.0) {
                vista.mostrarError("La nota final debe estar entre 0.0 y 5.0.");
                return;
            }
            estado = nota >= 3.0 ? "Aprobado" : "Reprobado";
        } else if (estado == null || estado.equals("Aprobado") || estado.equals("Reprobado")) {
            estado = "Matriculado";
        }

        InscripcionCurso nueva = new InscripcionCurso(idEstudiante, idGrupo, nota, estado);
        boolean exito = modeloDAO.matricular(nueva);

        if (exito) {
            vista.mostrarMensaje("✅ Estudiante matriculado exitosamente.");
            vista.limpiarFormulario();
            listarInscripciones();
        } else {
            vista.mostrarError("❌ No se pudo completar la matrícula.");
        }
    }

    private void actualizarInscripcion() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona una matrícula de la tabla para calificar o actualizar.");
            return;
        }

        try {
            int idInscripcion = Integer.parseInt(idStr);
            Double nota = parsearNota(vista.getTxtNotaFinal().getText().trim());
            String estado = (String) vista.getCmbEstado().getSelectedItem();

            if (nota != null) {
                if (nota < 0.0 || nota > 5.0) {
                    vista.mostrarError("La nota final debe estar entre 0.0 y 5.0.");
                    return;
                }
                estado = nota >= 3.0 ? "Aprobado" : "Reprobado";
            }

            InscripcionCurso modificada = new InscripcionCurso();
            modificada.setIdInscripcion(idInscripcion);
            modificada.setNotaFinal(nota);
            modificada.setEstado(estado);

            boolean exito = modeloDAO.actualizar(modificada);
            if (exito) {
                vista.mostrarMensaje("✅ Matrícula/Calificación actualizada con éxito.");
                vista.limpiarFormulario();
                listarInscripciones();
            } else {
                vista.mostrarError("❌ No se pudo actualizar el registro.");
            }
        } catch (NumberFormatException e) {
            vista.mostrarError("ID de inscripción no válido.");
        }
    }

    private void eliminarInscripcion() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona una matrícula de la tabla para eliminar o cancelar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("¿Estás seguro de cancelar o eliminar la matrícula con ID " + id + "?");
            if (confirmar) {
                boolean exito = modeloDAO.eliminar(id);
                if (exito) {
                    vista.mostrarMensaje("✅ Matrícula eliminada exitosamente.");
                    vista.limpiarFormulario();
                    listarInscripciones();
                } else {
                    vista.mostrarError("❌ No se pudo eliminar la matrícula.");
                }
            }
        } catch (NumberFormatException e) {
            vista.mostrarError("ID de inscripción no válido.");
        }
    }

    private void cargarInscripcionSeleccionada() {
        int fila = vista.getTblInscripciones().getSelectedRow();
        if (fila >= 0) {
            vista.getTxtId().setText(vista.getModeloTabla().getValueAt(fila, 0).toString());

            int idEst = Integer.parseInt(vista.getModeloTabla().getValueAt(fila, 1).toString());
            seleccionarEnCombo(vista.getCmbEstudiante(), idEst);

            int idGrp = Integer.parseInt(vista.getModeloTabla().getValueAt(fila, 3).toString());
            seleccionarEnCombo(vista.getCmbGrupo(), idGrp);

            Object notaObj = vista.getModeloTabla().getValueAt(fila, 5);
            if (notaObj != null && !notaObj.toString().equals("Sin Calificar")) {
                vista.getTxtNotaFinal().setText(notaObj.toString());
            } else {
                vista.getTxtNotaFinal().setText("");
            }

            Object estadoObj = vista.getModeloTabla().getValueAt(fila, 6);
            if (estadoObj != null) {
                vista.getCmbEstado().setSelectedItem(estadoObj.toString());
            }
        }
    }

    private int extraerIdDeCombo(String valorCombo) {
        String[] partes = valorCombo.split(" - ");
        return Integer.parseInt(partes[0].trim());
    }

    private void seleccionarEnCombo(javax.swing.JComboBox<String> combo, int idBuscado) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item.startsWith(idBuscado + " - ")) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private Double parsearNota(String textoNota) {
        if (textoNota == null || textoNota.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(textoNota.replace(",", "."));
        } catch (NumberFormatException e) {
            return -1.0; // Inválido
        }
    }
}
