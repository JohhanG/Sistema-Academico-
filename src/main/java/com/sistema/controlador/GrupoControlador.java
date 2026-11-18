package com.sistema.controlador;

import com.sistema.modelo.Docente;
import com.sistema.modelo.DocenteDAO;
import com.sistema.modelo.Grupo;
import com.sistema.modelo.GrupoDAO;
import com.sistema.modelo.Materia;
import com.sistema.modelo.MateriaDAO;
import com.sistema.servicio.GrupoService;
import com.sistema.vista.GrupoView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

// Controlador que conecta GrupoView y GrupoVista con GrupoService
public class GrupoControlador implements ActionListener {

    private final GrupoService servicio;
    private GrupoView vista;

    public GrupoControlador() {
        this.servicio = new GrupoService();
    }

    public GrupoControlador(GrupoDAO dao) {
        this.servicio = new GrupoService(dao);
    }

    public GrupoControlador(GrupoService servicio) {
        this.servicio = servicio;
    }

    public GrupoControlador(GrupoView vista) {
        this(vista, new GrupoService());
    }

    public GrupoControlador(GrupoView vista, GrupoService servicio) {
        this.vista = vista;
        this.servicio = servicio;
        inicializarEventos();
    }

    private void inicializarEventos() {
        if (this.vista == null) return;
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnActualizar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnRefrescar().addActionListener(this);

        this.vista.getTblGrupos().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    cargarGrupoSeleccionado();
                }
            }
        });
    }

    public void iniciar() {
        if (this.vista != null) {
            cargarCombos();
            listarGruposEnTabla();
            vista.setVisible(true);
        }
    }

    // Llena los combobox de Materia y Docente usando procedimientos almacenados
    public void cargarCombos() {
        if (vista == null) return;
        vista.getCmbMateria().removeAllItems();
        List<String> materiasSp = servicio.obtenerComboMaterias();
        if (!materiasSp.isEmpty()) {
            for (String item : materiasSp) {
                vista.getCmbMateria().addItem(item);
            }
        } else {
            for (Materia m : new MateriaDAO().listar()) {
                vista.getCmbMateria().addItem(m.getIdMateria() + " - " + m.getNombreMateria());
            }
        }

        vista.getCmbDocente().removeAllItems();
        List<String> docentesSp = servicio.obtenerComboDocentes();
        if (!docentesSp.isEmpty()) {
            for (String item : docentesSp) {
                vista.getCmbDocente().addItem(item);
            }
        } else {
            for (Docente d : new DocenteDAO().listar()) {
                vista.getCmbDocente().addItem(d.getIdDocente() + " - " + d.getNombre());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (vista == null) return;
        Object source = e.getSource();
        if (source == vista.getBtnGuardar()) {
            guardarDesdeVista();
        } else if (source == vista.getBtnActualizar()) {
            actualizarDesdeVista();
        } else if (source == vista.getBtnEliminar()) {
            eliminarDesdeVista();
        } else if (source == vista.getBtnLimpiar()) {
            vista.limpiarFormulario();
        } else if (source == vista.getBtnRefrescar()) {
            cargarCombos();
            listarGruposEnTabla();
            vista.mostrarMensaje("Lista de grupos actualizada.");
        }
    }

    public void listarGruposEnTabla() {
        if (vista == null) return;
        DefaultTableModel model = vista.getModeloTabla();
        model.setRowCount(0);
        try {
            for (Grupo g : listarGrupos()) {
                model.addRow(new Object[]{
                        g.getIdGrupo(),
                        g.getIdMateria(),
                        g.getIdDocente(),
                        g.getAula(),
                        g.getHorario()
                });
            }
        } catch (SQLException ex) {
            vista.mostrarError("Error al listar grupos: " + ex.getMessage());
        }
    }

    private void guardarDesdeVista() {
        String matItem = (String) vista.getCmbMateria().getSelectedItem();
        String docItem = (String) vista.getCmbDocente().getSelectedItem();
        if (matItem == null || docItem == null) {
            vista.mostrarError("Selecciona una Materia y un Docente.");
            return;
        }
        try {
            int idMateria = extraerIdDeCombo(matItem);
            int idDocente = extraerIdDeCombo(docItem);
            String aula = vista.getTxtAula().getText().trim();
            String horario = vista.getTxtHorario().getText().trim();

            int id = crearGrupo(idMateria, idDocente, aula, horario);
            if (id > 0) {
                vista.mostrarMensaje("Grupo creado exitosamente.");
                vista.limpiarFormulario();
                listarGruposEnTabla();
            } else {
                vista.mostrarError("No se pudo crear el grupo.");
            }
        } catch (IllegalArgumentException | SQLException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void actualizarDesdeVista() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona un grupo de la tabla para actualizar.");
            return;
        }
        String matItem = (String) vista.getCmbMateria().getSelectedItem();
        String docItem = (String) vista.getCmbDocente().getSelectedItem();
        if (matItem == null || docItem == null) {
            vista.mostrarError("Selecciona una Materia y un Docente.");
            return;
        }
        try {
            int idGrupo = Integer.parseInt(idStr);
            int idMateria = extraerIdDeCombo(matItem);
            int idDocente = extraerIdDeCombo(docItem);
            String aula = vista.getTxtAula().getText().trim();
            String horario = vista.getTxtHorario().getText().trim();

            Grupo grupo = new Grupo(idGrupo, idMateria, idDocente, aula, horario);
            if (actualizarGrupo(grupo)) {
                vista.mostrarMensaje("Grupo actualizado exitosamente.");
                vista.limpiarFormulario();
                listarGruposEnTabla();
            } else {
                vista.mostrarError("No se pudo actualizar el grupo.");
            }
        } catch (IllegalArgumentException | SQLException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void eliminarDesdeVista() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona un grupo de la tabla para eliminar.");
            return;
        }
        try {
            int idGrupo = Integer.parseInt(idStr);
            if (vista.confirmarAccion("Deseas eliminar el grupo con ID " + idGrupo + "?")) {
                if (eliminarGrupo(idGrupo)) {
                    vista.mostrarMensaje("Grupo eliminado exitosamente.");
                    vista.limpiarFormulario();
                    listarGruposEnTabla();
                } else {
                    vista.mostrarError("No se pudo eliminar el grupo.");
                }
            }
        } catch (IllegalArgumentException | SQLException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void cargarGrupoSeleccionado() {
        if (vista == null) return;
        int fila = vista.getTblGrupos().getSelectedRow();
        if (fila >= 0) {
            vista.getTxtId().setText(vista.getModeloTabla().getValueAt(fila, 0).toString());
            int idMat = Integer.parseInt(vista.getModeloTabla().getValueAt(fila, 1).toString());
            int idDoc = Integer.parseInt(vista.getModeloTabla().getValueAt(fila, 2).toString());
            seleccionarEnCombo(vista.getCmbMateria(), idMat);
            seleccionarEnCombo(vista.getCmbDocente(), idDoc);
            vista.getTxtAula().setText(vista.getModeloTabla().getValueAt(fila, 3).toString());
            vista.getTxtHorario().setText(vista.getModeloTabla().getValueAt(fila, 4).toString());
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

    public int crearGrupo(int idMateria, int idDocente, String aula, String horario) throws SQLException {
        return servicio.crearGrupo(idMateria, idDocente, aula, horario);
    }

    public List<Grupo> listarGrupos() throws SQLException {
        return servicio.listarGrupos();
    }

    public Grupo buscarGrupo(int idGrupo) throws SQLException {
        return servicio.buscarGrupoPorId(idGrupo);
    }

    public boolean actualizarGrupo(Grupo grupo) throws SQLException {
        return servicio.actualizarGrupo(grupo);
    }

    public boolean eliminarGrupo(int idGrupo) throws SQLException {
        return servicio.eliminarGrupo(idGrupo);
    }
}
