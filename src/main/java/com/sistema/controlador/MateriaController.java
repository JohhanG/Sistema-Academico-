package com.sistema.controlador;

import com.sistema.vista.TrabajoUI;

import com.sistema.modelo.Materia;
import com.sistema.modelo.MateriaDAO;
import com.sistema.servicio.MateriaService;
import com.sistema.vista.MateriaView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Controlador que conecta MateriaView con MateriaService
public class MateriaController implements ActionListener {

    private final MateriaView vista;
    private final MateriaService servicio;

    public MateriaController(MateriaView vista, MateriaDAO modelo) {
        this(vista, new MateriaService(modelo));
    }

    public MateriaController(MateriaView vista, MateriaService servicio) {
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

        this.vista.getTblMaterias().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarMateriaSeleccionada();
                }
            }
        });
    }

    public void iniciar() {
        listarMaterias();
        vista.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (TrabajoUI.ocupado(vista)) return;
        Object source = e.getSource();

        if (source == vista.getBtnGuardar()) {
            registrarMateria();
        } else if (source == vista.getBtnActualizar()) {
            actualizarMateria();
        } else if (source == vista.getBtnEliminar()) {
            eliminarMateria();
        } else if (source == vista.getBtnLimpiar()) {
            vista.limpiarFormulario();
        } else if (source == vista.getBtnRefrescar()) {
            listarMaterias();
        }
    }

    public void listarMaterias() {
        DefaultTableModel modelTabla = vista.getModeloTabla();
        modelTabla.setRowCount(0);

        TrabajoUI.ejecutar(vista, () -> servicio.listarMaterias(), lista -> {
        for (Materia mat : lista) {
            Object[] fila = {
                    mat.getIdMateria(),
                    mat.getNombreMateria(),
                    mat.getCreditos()
            };
            modelTabla.addRow(fila);
        }
        }, vista::mostrarError);
    }

    private void registrarMateria() {
        String nombre = vista.getTxtNombreMateria().getText().trim();
        int creditos = (int) vista.getSpnCreditos().getValue();

        try {
            Materia nuevaMateria = new Materia(nombre, creditos);
            TrabajoUI.ejecutar(vista, () -> servicio.registrarMateria(nuevaMateria), exito -> {
            if (exito) {
                vista.mostrarMensaje("Materia registrada exitosamente.");
                vista.limpiarFormulario();
                listarMaterias();
            } else {
                vista.mostrarError("No se pudo registrar la materia.");
            }
            }, vista::mostrarError);
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void actualizarMateria() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona una materia de la tabla para actualizar.");
            return;
        }

        String nombre = vista.getTxtNombreMateria().getText().trim();
        int creditos = (int) vista.getSpnCreditos().getValue();

        try {
            int id = Integer.parseInt(idStr);
            Materia materia = new Materia(id, nombre, creditos);
            TrabajoUI.ejecutar(vista, () -> servicio.actualizarMateria(materia), exito -> {
            if (exito) {
                vista.mostrarMensaje("Materia actualizada exitosamente.");
                vista.limpiarFormulario();
                listarMaterias();
            } else {
                vista.mostrarError("No se pudo actualizar la materia.");
            }
            }, vista::mostrarError);
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de materia invalido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void eliminarMateria() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona una materia de la tabla para eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("Deseas eliminar la materia con ID " + id + "?");
            if (confirmar) {
                TrabajoUI.ejecutar(vista, () -> servicio.eliminarMateria(id), exito -> {
            if (exito) {
                    vista.mostrarMensaje("Materia eliminada exitosamente.");
                    vista.limpiarFormulario();
                    listarMaterias();
                } else {
                    vista.mostrarError("No se pudo eliminar la materia.");
                }
            }, vista::mostrarError);
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de materia invalido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void cargarMateriaSeleccionada() {
        int fila = vista.getTblMaterias().getSelectedRow();
        if (fila >= 0) {
            vista.getTxtId().setText(vista.getModeloTabla().getValueAt(fila, 0).toString());
            vista.getTxtNombreMateria().setText(vista.getModeloTabla().getValueAt(fila, 1).toString());
            try {
                int creditos = Integer.parseInt(vista.getModeloTabla().getValueAt(fila, 2).toString());
                vista.getSpnCreditos().setValue(creditos);
            } catch (Exception ignored) {
            }
        }
    }
}
