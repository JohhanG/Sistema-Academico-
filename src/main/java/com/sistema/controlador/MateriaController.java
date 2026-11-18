package com.sistema.controlador;

import com.sistema.modelo.Materia;
import com.sistema.modelo.MateriaDAO;
import com.sistema.vista.MateriaView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controlador para la gestión de Materias en el patrón MVC.
 * Coordina los eventos de la vista con la capa de persistencia (DAO).
 */
public class MateriaController implements ActionListener {

    private final MateriaView vista;
    private final MateriaDAO modelo;

    public MateriaController(MateriaView vista, MateriaDAO modelo) {
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
            vista.mostrarMensaje("Lista de materias actualizada correctamente.");
        }
    }

    public void listarMaterias() {
        DefaultTableModel modelTabla = vista.getModeloTabla();
        modelTabla.setRowCount(0);

        List<Materia> lista = modelo.listar();
        for (Materia mat : lista) {
            Object[] fila = {
                    mat.getIdMateria(),
                    mat.getNombreMateria(),
                    mat.getCreditos()
            };
            modelTabla.addRow(fila);
        }
    }

    private void registrarMateria() {
        String nombre = vista.getTxtNombreMateria().getText().trim();
        int creditos = (int) vista.getSpnCreditos().getValue();

        if (!validarCampos(nombre, creditos)) {
            return;
        }

        Materia nuevaMateria = new Materia(nombre, creditos);
        boolean exito = modelo.insertar(nuevaMateria);

        if (exito) {
            vista.mostrarMensaje("✅ Materia registrada exitosamente.");
            vista.limpiarFormulario();
            listarMaterias();
        } else {
            vista.mostrarError("❌ No se pudo registrar la materia en la base de datos.");
        }
    }

    private void actualizarMateria() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Por favor selecciona una materia de la tabla para actualizar.");
            return;
        }

        String nombre = vista.getTxtNombreMateria().getText().trim();
        int creditos = (int) vista.getSpnCreditos().getValue();

        if (!validarCampos(nombre, creditos)) {
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Materia materia = new Materia(id, nombre, creditos);
            boolean exito = modelo.actualizar(materia);

            if (exito) {
                vista.mostrarMensaje("✅ Materia actualizada exitosamente.");
                vista.limpiarFormulario();
                listarMaterias();
            } else {
                vista.mostrarError("❌ No se pudo actualizar la materia.");
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de materia inválido.");
        }
    }

    private void eliminarMateria() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Por favor selecciona una materia de la tabla para eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("¿Estás seguro de eliminar la materia con ID " + id + "?");
            if (confirmar) {
                boolean exito = modelo.eliminar(id);
                if (exito) {
                    vista.mostrarMensaje("✅ Materia eliminada exitosamente.");
                    vista.limpiarFormulario();
                    listarMaterias();
                } else {
                    vista.mostrarError("❌ No se pudo eliminar la materia. Puede tener grupos o cursos asignados.");
                }
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de materia inválido.");
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
            } catch (Exception ignored) {}
        }
    }

    private boolean validarCampos(String nombre, int creditos) {
        if (nombre.isEmpty()) {
            vista.mostrarError("El nombre de la materia es obligatorio.");
            return false;
        }
        if (creditos <= 0) {
            vista.mostrarError("Los créditos deben ser mayores a 0.");
            return false;
        }
        return true;
    }
}
