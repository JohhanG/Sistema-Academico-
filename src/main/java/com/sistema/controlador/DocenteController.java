package com.sistema.controlador;

import com.sistema.modelo.Docente;
import com.sistema.modelo.DocenteDAO;
import com.sistema.servicio.DocenteService;
import com.sistema.vista.DocenteView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Controlador que conecta DocenteView con DocenteService
public class DocenteController implements ActionListener {

    private final DocenteView vista;
    private final DocenteService servicio;

    public DocenteController(DocenteView vista, DocenteDAO modelo) {
        this(vista, new DocenteService(modelo));
    }

    public DocenteController(DocenteView vista, DocenteService servicio) {
        this.vista = vista;
        this.servicio = servicio;
        inicializarEventos();
    }

    private void inicializarEventos() {
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnActualizar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnRefrescar().addActionListener(this);

        this.vista.getTblDocentes().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarDocenteSeleccionado();
                }
            }
        });
    }

    public void iniciar() {
        listarDocentes();
        vista.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == vista.getBtnGuardar()) {
            registrarDocente();
        } else if (source == vista.getBtnActualizar()) {
            actualizarDocente();
        } else if (source == vista.getBtnEliminar()) {
            eliminarDocente();
        } else if (source == vista.getBtnLimpiar()) {
            vista.limpiarFormulario();
        } else if (source == vista.getBtnRefrescar()) {
            listarDocentes();
            vista.mostrarMensaje("Lista de docentes actualizada.");
        }
    }

    public void listarDocentes() {
        DefaultTableModel modelTabla = vista.getModeloTabla();
        modelTabla.setRowCount(0);

        List<Docente> lista = servicio.listarDocentes();
        for (Docente doc : lista) {
            Object[] fila = {
                    doc.getIdDocente(),
                    doc.getNombre(),
                    doc.getEspecialidad()
            };
            modelTabla.addRow(fila);
        }
    }

    private void registrarDocente() {
        String nombre = vista.getTxtNombre().getText().trim();
        String especialidad = vista.getTxtEspecialidad().getText().trim();

        try {
            Docente nuevoDocente = new Docente(nombre, especialidad);
            boolean exito = servicio.registrarDocente(nuevoDocente);

            if (exito) {
                vista.mostrarMensaje("Docente registrado exitosamente.");
                vista.limpiarFormulario();
                listarDocentes();
            } else {
                vista.mostrarError("No se pudo registrar el docente.");
            }
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void actualizarDocente() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona un docente de la tabla para actualizar.");
            return;
        }

        String nombre = vista.getTxtNombre().getText().trim();
        String especialidad = vista.getTxtEspecialidad().getText().trim();

        try {
            int id = Integer.parseInt(idStr);
            Docente docente = new Docente(id, nombre, especialidad);
            boolean exito = servicio.actualizarDocente(docente);

            if (exito) {
                vista.mostrarMensaje("Docente actualizado exitosamente.");
                vista.limpiarFormulario();
                listarDocentes();
            } else {
                vista.mostrarError("No se pudo actualizar el docente.");
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de docente invalido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void eliminarDocente() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona un docente de la tabla para eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("Deseas eliminar el docente con ID " + id + "?");
            if (confirmar) {
                boolean exito = servicio.eliminarDocente(id);
                if (exito) {
                    vista.mostrarMensaje("Docente eliminado exitosamente.");
                    vista.limpiarFormulario();
                    listarDocentes();
                } else {
                    vista.mostrarError("No se pudo eliminar el docente.");
                }
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de docente invalido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void cargarDocenteSeleccionado() {
        int fila = vista.getTblDocentes().getSelectedRow();
        if (fila >= 0) {
            vista.getTxtId().setText(vista.getModeloTabla().getValueAt(fila, 0).toString());
            vista.getTxtNombre().setText(vista.getModeloTabla().getValueAt(fila, 1).toString());
            vista.getTxtEspecialidad().setText(vista.getModeloTabla().getValueAt(fila, 2).toString());
        }
    }
}
