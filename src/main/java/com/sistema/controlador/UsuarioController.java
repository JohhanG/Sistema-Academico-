package com.sistema.controlador;

import com.sistema.modelo.Usuario;
import com.sistema.modelo.Estudiante;
import com.sistema.modelo.Docente;
import com.sistema.servicio.UsuarioService;
import com.sistema.vista.UsuarioView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Controlador que conecta UsuarioView con UsuarioService
public class UsuarioController implements ActionListener {

    private final UsuarioView vista;
    private final UsuarioService servicio;

    public UsuarioController(UsuarioView vista, UsuarioService servicio) {
        this.vista = vista;
        this.servicio = servicio;
        inicializarEventos();
    }

    private void inicializarEventos() {
        vista.getCmbRol().addActionListener(e -> actualizarCamposPorRol());
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnActualizar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnRefrescar().addActionListener(this);

        this.vista.getTblUsuarios().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarUsuarioSeleccionado();
                }
            }
        });
    }

    public void iniciar() {
        cargarAsociaciones();
        actualizarCamposPorRol();
        listarUsuarios();
        vista.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == vista.getBtnGuardar()) {
            registrarUsuario();
        } else if (source == vista.getBtnActualizar()) {
            actualizarUsuario();
        } else if (source == vista.getBtnEliminar()) {
            eliminarUsuario();
        } else if (source == vista.getBtnLimpiar()) {
            vista.limpiarFormulario();
        } else if (source == vista.getBtnRefrescar()) {
            vista.limpiarFormulario();
            cargarAsociaciones();
            listarUsuarios();
            vista.mostrarMensaje("Lista de usuarios actualizada.");
        }
    }

    public void listarUsuarios() {
        DefaultTableModel modelTabla = vista.getModeloTabla();
        modelTabla.setRowCount(0);

        List<Usuario> lista = servicio.listarUsuarios();
        for (Usuario u : lista) {
            Object[] fila = {
                    u.getIdUsuario(),
                    u.getUsername(),
                    u.getRol(),
                    u.getIdEstudiante(),
                    u.getIdDocente()
            };
            modelTabla.addRow(fila);
        }
    }

    private void registrarUsuario() {
        String username = vista.getTxtUsername().getText().trim();
        String password = new String(vista.getTxtPassword().getPassword());
        String rol = (String) vista.getCmbRol().getSelectedItem();

        try {
            Usuario nuevo = new Usuario(username, password, rol);
            asignarAsociacion(nuevo);
            boolean exito = servicio.registrarUsuario(nuevo);
            if (exito) {
                vista.mostrarMensaje("Usuario registrado exitosamente.");
                vista.limpiarFormulario();
                listarUsuarios();
            } else {
                vista.mostrarError("No se pudo registrar el usuario.");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void actualizarUsuario() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona un usuario de la tabla para actualizar.");
            return;
        }

        String username = vista.getTxtUsername().getText().trim();
        String password = new String(vista.getTxtPassword().getPassword());
        String rol = (String) vista.getCmbRol().getSelectedItem();

        try {
            int id = Integer.parseInt(idStr);
            Usuario actual = servicio.buscarPorId(id);
            String passFinal = password;
            if ((password == null || password.isEmpty()) && actual != null) {
                passFinal = actual.getPassword();
            }

            Usuario modificado = new Usuario(id, username, passFinal, rol,
                    actual != null ? actual.getIdEstudiante() : null,
                    actual != null ? actual.getIdDocente() : null);
            asignarAsociacion(modificado);

            boolean exito = servicio.actualizarUsuario(modificado);
            if (exito) {
                vista.mostrarMensaje("Usuario actualizado exitosamente.");
                vista.limpiarFormulario();
                listarUsuarios();
            } else {
                vista.mostrarError("No se pudo actualizar el usuario.");
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de usuario invalido.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void eliminarUsuario() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona un usuario de la tabla para eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            if (vista.confirmarAccion("Deseas eliminar el usuario con ID " + id + "?")) {
                boolean exito = servicio.eliminarUsuario(id);
                if (exito) {
                    vista.mostrarMensaje("Usuario eliminado exitosamente.");
                    vista.limpiarFormulario();
                    listarUsuarios();
                } else {
                    vista.mostrarError("No se pudo eliminar el usuario.");
                }
            }
        } catch (NumberFormatException ex) {
            vista.mostrarError("ID de usuario invalido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void cargarUsuarioSeleccionado() {
        int fila = vista.getTblUsuarios().getSelectedRow();
        if (fila >= 0) {
            vista.getTxtId().setText(vista.getModeloTabla().getValueAt(fila, 0).toString());
            vista.getTxtUsername().setText(vista.getModeloTabla().getValueAt(fila, 1).toString());
            vista.getTxtPassword().setText("");
            vista.getCmbRol().setSelectedItem(vista.getModeloTabla().getValueAt(fila, 2).toString());
            seleccionarAsociacion(vista.getCmbEstudiante(), vista.getModeloTabla().getValueAt(fila, 3));
            seleccionarAsociacion(vista.getCmbDocente(), vista.getModeloTabla().getValueAt(fila, 4));
        }
    }

    private void cargarAsociaciones() {
        vista.getCmbEstudiante().removeAllItems();
        vista.getCmbDocente().removeAllItems();
        for (Estudiante e : servicio.listarEstudiantes()) {
            vista.getCmbEstudiante().addItem(e.getIdEstudiante() + " - " + e.getNombre() + " " + e.getApellido());
        }
        for (Docente d : servicio.listarDocentes()) {
            vista.getCmbDocente().addItem(d.getIdDocente() + " - " + d.getNombre());
        }
        vista.getCmbEstudiante().setSelectedIndex(-1);
        vista.getCmbDocente().setSelectedIndex(-1);
    }

    private void actualizarCamposPorRol() {
        String rol = (String) vista.getCmbRol().getSelectedItem();
        vista.getCmbEstudiante().setEnabled("ESTUDIANTE".equals(rol));
        vista.getCmbDocente().setEnabled("DOCENTE".equals(rol));
        if (!"ESTUDIANTE".equals(rol)) vista.getCmbEstudiante().setSelectedIndex(-1);
        if (!"DOCENTE".equals(rol)) vista.getCmbDocente().setSelectedIndex(-1);
    }

    private void asignarAsociacion(Usuario usuario) {
        usuario.setIdEstudiante("ESTUDIANTE".equals(usuario.getRol())
                ? idSeleccionado(vista.getCmbEstudiante()) : null);
        usuario.setIdDocente("DOCENTE".equals(usuario.getRol())
                ? idSeleccionado(vista.getCmbDocente()) : null);
    }

    private Integer idSeleccionado(javax.swing.JComboBox<String> combo) {
        String item = (String) combo.getSelectedItem();
        return item == null ? null : Integer.valueOf(item.split(" - ", 2)[0]);
    }

    private void seleccionarAsociacion(javax.swing.JComboBox<String> combo, Object id) {
        combo.setSelectedIndex(-1);
        if (id == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).startsWith(id + " - ")) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }
}
