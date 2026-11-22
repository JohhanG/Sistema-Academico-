package com.sistema.controlador;
import com.sistema.vista.TrabajoUI;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;
import com.sistema.servicio.EstudianteService;
import com.sistema.vista.EstudianteView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

// Controlador que conecta EstudianteView con EstudianteService
public class EstudianteController implements ActionListener {

    private final EstudianteService estudianteService;
    private EstudianteView vista;

    public EstudianteController() {
        this.estudianteService = new EstudianteService();
    }

    public EstudianteController(EstudianteDao estudianteDao) {
        this.estudianteService = new EstudianteService(estudianteDao);
    }

    public EstudianteController(EstudianteService estudianteService) {
        if (estudianteService == null) {
            throw new IllegalArgumentException("El servicio de estudiante no puede ser nulo.");
        }
        this.estudianteService = estudianteService;
    }

    public EstudianteController(EstudianteView vista, EstudianteDao estudianteDao) {
        this(estudianteDao);
        this.vista = vista;
        inicializarEventos();
    }

    public EstudianteController(EstudianteView vista, EstudianteService estudianteService) {
        this(estudianteService);
        this.vista = vista;
        inicializarEventos();
    }

    private void inicializarEventos() {
        if (this.vista == null) return;

        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnActualizar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnRefrescar().addActionListener(this);

        this.vista.getTblEstudiantes().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarEstudianteSeleccionado();
                }
            }
        });
    }

    public void iniciar() {
        if (this.vista != null) {
            listarEstudiantesEnTabla();
            vista.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (TrabajoUI.ocupado(vista)) return;
        if (vista == null) return;
        Object source = e.getSource();

        if (source == vista.getBtnGuardar()) {
            ejecutarRegistroDesdeVista();
        } else if (source == vista.getBtnActualizar()) {
            ejecutarActualizacionDesdeVista();
        } else if (source == vista.getBtnEliminar()) {
            ejecutarEliminacionDesdeVista();
        } else if (source == vista.getBtnLimpiar()) {
            vista.limpiarFormulario();
        } else if (source == vista.getBtnRefrescar()) {
            listarEstudiantesEnTabla();

        }
    }

    public void listarEstudiantesEnTabla() {
        if (vista==null) return;
        vista.getModeloTabla().setRowCount(0);
        TrabajoUI.ejecutar(vista, this::obtenerTodosLosEstudiantes, lista -> {
            for (Estudiante e:lista) vista.getModeloTabla().addRow(new Object[]{e.getIdEstudiante(),e.getNombre(),e.getApellido(),e.getEmail()});
        },vista::mostrarError);
    }

    private void ejecutarRegistroDesdeVista() {
        String nombre = vista.getTxtNombre().getText().trim();
        String apellido = vista.getTxtApellido().getText().trim();
        String email = vista.getTxtEmail().getText().trim();

        try {
            TrabajoUI.ejecutar(vista, () -> { registrarEstudiante(nombre, apellido, email); return true; }, resultado -> {
                vista.mostrarMensaje("Estudiante registrado exitosamente."); vista.limpiarFormulario(); listarEstudiantesEnTabla();
            },vista::mostrarError);
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error al registrar estudiante: " + ex.getMessage());
        }
    }

    private void ejecutarActualizacionDesdeVista() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona un estudiante de la tabla para actualizar.");
            return;
        }

        String nombre = vista.getTxtNombre().getText().trim();
        String apellido = vista.getTxtApellido().getText().trim();
        String email = vista.getTxtEmail().getText().trim();

        try {
            int id = Integer.parseInt(idStr);
            TrabajoUI.ejecutar(vista, () -> { actualizarEstudiante(id, nombre, apellido, email); return true; }, resultado -> {
                vista.mostrarMensaje("Estudiante actualizado exitosamente."); vista.limpiarFormulario(); listarEstudiantesEnTabla();
            },vista::mostrarError);
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de estudiante invalido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error al actualizar estudiante: " + ex.getMessage());
        }
    }

    private void ejecutarEliminacionDesdeVista() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona un estudiante de la tabla para eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("Deseas eliminar el estudiante con ID " + id + "?");
            if (confirmar) {
                TrabajoUI.ejecutar(vista, () -> { eliminarEstudiante(id); return true; }, resultado -> {
                vista.mostrarMensaje("Estudiante eliminado exitosamente."); vista.limpiarFormulario(); listarEstudiantesEnTabla();
            },vista::mostrarError);
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de estudiante invalido.");
        } catch (Exception ex) {
            vista.mostrarError("Error al eliminar estudiante: " + ex.getMessage());
        }
    }

    private void cargarEstudianteSeleccionado() {
        if (vista == null) return;
        int fila = vista.getTblEstudiantes().getSelectedRow();
        if (fila >= 0) {
            vista.getTxtId().setText(vista.getModeloTabla().getValueAt(fila, 0).toString());
            vista.getTxtNombre().setText(vista.getModeloTabla().getValueAt(fila, 1).toString());
            vista.getTxtApellido().setText(vista.getModeloTabla().getValueAt(fila, 2).toString());
            vista.getTxtEmail().setText(vista.getModeloTabla().getValueAt(fila, 3).toString());
        }
    }

    public Estudiante registrarEstudiante(String nombre, String apellido, String email) {
        return estudianteService.registrarEstudiante(nombre, apellido, email);
    }

    public void registrarEstudiante(Estudiante estudiante) {
        estudianteService.registrarEstudiante(estudiante);
    }

    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteService.obtenerTodosLosEstudiantes();
    }

    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        return estudianteService.buscarEstudiantePorId(id);
    }

    public Optional<Estudiante> buscarEstudiantePorEmail(String email) {
        return estudianteService.buscarEstudiantePorEmail(email);
    }

    public void actualizarEstudiante(int id, String nombre, String apellido, String email) {
        estudianteService.actualizarEstudiante(id, nombre, apellido, email);
    }

    public void actualizarEstudiante(Estudiante estudiante) {
        estudianteService.actualizarEstudiante(estudiante);
    }

    public void eliminarEstudiante(int id) {
        estudianteService.eliminarEstudiante(id);
    }
}
