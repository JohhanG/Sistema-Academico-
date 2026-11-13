package com.sistema.controlador;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDAO;
import com.sistema.vista.EstudianteView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controlador para la gestión de Estudiantes en el patrón MVC.
 * Gestiona los eventos de la vista y coordina las operaciones con el modelo (DAO).
 */
public class EstudianteController implements ActionListener {

    private final EstudianteView vista;
    private final EstudianteDAO modelo;

    public EstudianteController(EstudianteView vista, EstudianteDAO modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicializarEventos();
    }

    /**
     * Asocia los oyentes (listeners) a los componentes de la vista.
     */
    private void inicializarEventos() {
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnActualizar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnRefrescar().addActionListener(this);

        // Evento de selección en la tabla
        this.vista.getTblEstudiantes().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarEstudianteSeleccionado();
                }
            }
        });
    }

    /**
     * Inicializa la vista y carga los datos desde la base de datos.
     */
    public void iniciar() {
        listarEstudiantes();
        vista.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == vista.getBtnGuardar()) {
            registrarEstudiante();
        } else if (source == vista.getBtnActualizar()) {
            actualizarEstudiante();
        } else if (source == vista.getBtnEliminar()) {
            eliminarEstudiante();
        } else if (source == vista.getBtnLimpiar()) {
            vista.limpiarFormulario();
        } else if (source == vista.getBtnRefrescar()) {
            listarEstudiantes();
            vista.mostrarMensaje("Lista de estudiantes actualizada correctamente.");
        }
    }

    /**
     * Carga todos los estudiantes en la tabla de la vista.
     */
    public void listarEstudiantes() {
        DefaultTableModel modelTabla = vista.getModeloTabla();
        modelTabla.setRowCount(0); // Limpiar filas previas

        List<Estudiante> lista = modelo.listar();
        for (Estudiante est : lista) {
            Object[] fila = {
                    est.getIdEstudiante(),
                    est.getNombre(),
                    est.getApellido(),
                    est.getEmail()
            };
            modelTabla.addRow(fila);
        }
    }

    /**
     * Registra un nuevo estudiante a partir de los datos ingresados.
     */
    private void registrarEstudiante() {
        String nombre = vista.getTxtNombre().getText().trim();
        String apellido = vista.getTxtApellido().getText().trim();
        String email = vista.getTxtEmail().getText().trim();

        if (!validarCampos(nombre, apellido, email)) {
            return;
        }

        Estudiante nuevoEstudiante = new Estudiante(nombre, apellido, email);
        boolean exito = modelo.insertar(nuevoEstudiante);

        if (exito) {
            vista.mostrarMensaje("✅ Estudiante registrado exitosamente.");
            vista.limpiarFormulario();
            listarEstudiantes();
        } else {
            vista.mostrarError("❌ No se pudo registrar el estudiante. Verifica que el correo no esté duplicado.");
        }
    }

    /**
     * Actualiza la información del estudiante actualmente seleccionado.
     */
    private void actualizarEstudiante() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Por favor selecciona un estudiante de la tabla para actualizar.");
            return;
        }

        String nombre = vista.getTxtNombre().getText().trim();
        String apellido = vista.getTxtApellido().getText().trim();
        String email = vista.getTxtEmail().getText().trim();

        if (!validarCampos(nombre, apellido, email)) {
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Estudiante estudiante = new Estudiante(id, nombre, apellido, email);
            boolean exito = modelo.actualizar(estudiante);

            if (exito) {
                vista.mostrarMensaje("✅ Estudiante actualizado exitosamente.");
                vista.limpiarFormulario();
                listarEstudiantes();
            } else {
                vista.mostrarError("❌ No se pudo actualizar el estudiante.");
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de estudiante inválido.");
        }
    }

    /**
     * Elimina el estudiante seleccionado tras confirmar con el usuario.
     */
    private void eliminarEstudiante() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Por favor selecciona un estudiante de la tabla para eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("¿Estás seguro de eliminar el estudiante con ID " + id + "?");
            if (confirmar) {
                boolean exito = modelo.eliminar(id);
                if (exito) {
                    vista.mostrarMensaje("✅ Estudiante eliminado exitosamente.");
                    vista.limpiarFormulario();
                    listarEstudiantes();
                } else {
                    vista.mostrarError("❌ No se pudo eliminar el estudiante. Puede tener inscripciones asociadas.");
                }
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de estudiante inválido.");
        }
    }

    /**
     * Llena los campos del formulario con el estudiante seleccionado de la tabla.
     */
    private void cargarEstudianteSeleccionado() {
        int fila = vista.getTblEstudiantes().getSelectedRow();
        if (fila >= 0) {
            vista.getTxtId().setText(vista.getModeloTabla().getValueAt(fila, 0).toString());
            vista.getTxtNombre().setText(vista.getModeloTabla().getValueAt(fila, 1).toString());
            vista.getTxtApellido().setText(vista.getModeloTabla().getValueAt(fila, 2).toString());
            vista.getTxtEmail().setText(vista.getModeloTabla().getValueAt(fila, 3).toString());
        }
    }

    /**
     * Valida que los campos no estén vacíos y tengan un formato coherente.
     */
    private boolean validarCampos(String nombre, String apellido, String email) {
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            vista.mostrarError("Todos los campos (Nombre, Apellido, Correo) son obligatorios.");
            return false;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            vista.mostrarError("El formato del correo electrónico no es válido.");
            return false;
        }

        return true;
    }
}

