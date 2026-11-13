package com.sistema.controlador;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;
import com.sistema.vista.EstudianteView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Controlador para la entidad Estudiante.
 * Orquesta la lógica de negocio, validaciones y la persistencia (DAO),
 * además de gestionar los eventos de la vista gráfica cuando está presente.
 */
public class EstudianteController implements ActionListener {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final EstudianteDao estudianteDao;
    private EstudianteView vista;

    // Constructor sin vista (solo lógica de negocio / servicios)
    public EstudianteController() {
        this.estudianteDao = new EstudianteDao();
    }

    public EstudianteController(EstudianteDao estudianteDao) {
        if (estudianteDao == null) {
            throw new IllegalArgumentException("El DAO de estudiante no puede ser nulo.");
        }
        this.estudianteDao = estudianteDao;
    }

    // Constructor completo con Vista (para GUI Swing)
    public EstudianteController(EstudianteView vista, EstudianteDao estudianteDao) {
        this(estudianteDao);
        this.vista = vista;
        inicializarEventos();
    }

    // =========================================================================
    // MÉTODOS DE LA VISTA (INTERFAZ GRÁFICA SWING)
    // =========================================================================

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
            vista.mostrarMensaje("Lista de estudiantes actualizada correctamente.");
        }
    }

    public void listarEstudiantesEnTabla() {
        if (vista == null) return;
        DefaultTableModel modelTabla = vista.getModeloTabla();
        modelTabla.setRowCount(0);

        List<Estudiante> lista = obtenerTodosLosEstudiantes();
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

    private void ejecutarRegistroDesdeVista() {
        String nombre = vista.getTxtNombre().getText().trim();
        String apellido = vista.getTxtApellido().getText().trim();
        String email = vista.getTxtEmail().getText().trim();

        try {
            registrarEstudiante(nombre, apellido, email);
            vista.mostrarMensaje("✅ Estudiante registrado exitosamente.");
            vista.limpiarFormulario();
            listarEstudiantesEnTabla();
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("❌ Error al registrar estudiante: " + ex.getMessage());
        }
    }

    private void ejecutarActualizacionDesdeVista() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Por favor selecciona un estudiante de la tabla para actualizar.");
            return;
        }

        String nombre = vista.getTxtNombre().getText().trim();
        String apellido = vista.getTxtApellido().getText().trim();
        String email = vista.getTxtEmail().getText().trim();

        try {
            int id = Integer.parseInt(idStr);
            actualizarEstudiante(id, nombre, apellido, email);
            vista.mostrarMensaje("✅ Estudiante actualizado exitosamente.");
            vista.limpiarFormulario();
            listarEstudiantesEnTabla();
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de estudiante inválido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("❌ Error al actualizar estudiante: " + ex.getMessage());
        }
    }

    private void ejecutarEliminacionDesdeVista() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Por favor selecciona un estudiante de la tabla para eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("¿Estás seguro de eliminar el estudiante con ID " + id + "?");
            if (confirmar) {
                eliminarEstudiante(id);
                vista.mostrarMensaje("✅ Estudiante eliminado exitosamente.");
                vista.limpiarFormulario();
                listarEstudiantesEnTabla();
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de estudiante inválido.");
        } catch (Exception ex) {
            vista.mostrarError("❌ Error al eliminar estudiante: " + ex.getMessage());
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

    // =========================================================================
    // LÓGICA DE NEGOCIO Y PERSISTENCIA (SANTIAGO)
    // =========================================================================

    public Estudiante registrarEstudiante(String nombre, String apellido, String email) {
        Estudiante estudiante = new Estudiante(
                nombre != null ? nombre.trim() : null,
                apellido != null ? apellido.trim() : null,
                email != null ? email.trim() : null
        );
        registrarEstudiante(estudiante);
        return estudiante;
    }

    public void registrarEstudiante(Estudiante estudiante) {
        validarEstudiante(estudiante);

        Optional<Estudiante> existente = estudianteDao.buscarPorEmail(estudiante.getEmail());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el email: " + estudiante.getEmail());
        }

        estudianteDao.guardar(estudiante);
    }

    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del estudiante debe ser mayor a cero.");
        }
        return estudianteDao.buscarPorId(id);
    }

    public Optional<Estudiante> buscarEstudiantePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email de búsqueda no puede estar vacío.");
        }
        return estudianteDao.buscarPorEmail(email.trim());
    }

    public void actualizarEstudiante(int id, String nombre, String apellido, String email) {
        Estudiante estudiante = new Estudiante(
                id,
                nombre != null ? nombre.trim() : null,
                apellido != null ? apellido.trim() : null,
                email != null ? email.trim() : null
        );
        actualizarEstudiante(estudiante);
    }

    public void actualizarEstudiante(Estudiante estudiante) {
        if (estudiante == null || estudiante.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("El estudiante o su ID es inválido para actualizar.");
        }

        validarEstudiante(estudiante);

        Optional<Estudiante> existente = estudianteDao.buscarPorId(estudiante.getIdEstudiante());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el estudiante con ID: " + estudiante.getIdEstudiante());
        }

        Optional<Estudiante> porEmail = estudianteDao.buscarPorEmail(estudiante.getEmail());
        if (porEmail.isPresent() && porEmail.get().getIdEstudiante() != estudiante.getIdEstudiante()) {
            throw new IllegalArgumentException("El email ya está en uso por otro estudiante: " + estudiante.getEmail());
        }

        estudianteDao.actualizar(estudiante);
    }

    public void eliminarEstudiante(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del estudiante debe ser mayor a cero.");
        }

        Optional<Estudiante> existente = estudianteDao.buscarPorId(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el estudiante con ID: " + id);
        }

        estudianteDao.eliminar(id);
    }

    private void validarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (estudiante.getNombre() == null || estudiante.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estudiante es obligatorio.");
        }
        if (estudiante.getApellido() == null || estudiante.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del estudiante es obligatorio.");
        }
        if (estudiante.getEmail() == null || estudiante.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del estudiante es obligatorio.");
        }
        if (!EMAIL_PATTERN.matcher(estudiante.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("El formato del email no es válido.");
        }
    }
}
