package com.sistema.controlador;

import com.sistema.modelo.Docente;
import com.sistema.modelo.DocenteDAO;
import com.sistema.vista.DocenteView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controlador para la gestión de Docentes en el patrón MVC.
 * Coordina los eventos de la vista con la capa de persistencia (DAO).
 */
public class DocenteController implements ActionListener {

    private final DocenteView vista;
    private final DocenteDAO modelo;

    public DocenteController(DocenteView vista, DocenteDAO modelo) {
        this.vista = vista;
        this.modelo = modelo;
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
            vista.mostrarMensaje("Lista de docentes actualizada correctamente.");
        }
    }

    public void listarDocentes() {
        DefaultTableModel modelTabla = vista.getModeloTabla();
        modelTabla.setRowCount(0);

        List<Docente> lista = modelo.listar();
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

        if (!validarCampos(nombre, especialidad)) {
            return;
        }

        Docente nuevoDocente = new Docente(nombre, especialidad);
        boolean exito = modelo.insertar(nuevoDocente);

        if (exito) {
            vista.mostrarMensaje("✅ Docente registrado exitosamente.");
            vista.limpiarFormulario();
            listarDocentes();
        } else {
            vista.mostrarError("❌ No se pudo registrar el docente en la base de datos.");
        }
    }

    private void actualizarDocente() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Por favor selecciona un docente de la tabla para actualizar.");
            return;
        }

        String nombre = vista.getTxtNombre().getText().trim();
        String especialidad = vista.getTxtEspecialidad().getText().trim();

        if (!validarCampos(nombre, especialidad)) {
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Docente docente = new Docente(id, nombre, especialidad);
            boolean exito = modelo.actualizar(docente);

            if (exito) {
                vista.mostrarMensaje("✅ Docente actualizado exitosamente.");
                vista.limpiarFormulario();
                listarDocentes();
            } else {
                vista.mostrarError("❌ No se pudo actualizar el docente.");
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de docente inválido.");
        }
    }

    private void eliminarDocente() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Por favor selecciona un docente de la tabla para eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("¿Estás seguro de eliminar el docente con ID " + id + "?");
            if (confirmar) {
                boolean exito = modelo.eliminar(id);
                if (exito) {
                    vista.mostrarMensaje("✅ Docente eliminado exitosamente.");
                    vista.limpiarFormulario();
                    listarDocentes();
                } else {
                    vista.mostrarError("❌ No se pudo eliminar el docente. Puede tener grupos o cursos asignados.");
                }
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de docente inválido.");
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

    private boolean validarCampos(String nombre, String especialidad) {
        if (nombre.isEmpty() || especialidad.isEmpty()) {
            vista.mostrarError("Todos los campos (Nombre y Especialidad) son obligatorios.");
            return false;
        }
        return true;
    }
}
