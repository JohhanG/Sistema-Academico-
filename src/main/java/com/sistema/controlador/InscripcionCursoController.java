package com.sistema.controlador;

import com.sistema.vista.TrabajoUI;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;
import com.sistema.modelo.Grupo;
import com.sistema.modelo.GrupoDAO;
import com.sistema.modelo.InscripcionCurso;
import com.sistema.modelo.InscripcionCursoDAO;
import com.sistema.modelo.Usuario;
import com.sistema.servicio.InscripcionCursoService;
import com.sistema.servicio.IdentidadAcademicaService;
import com.sistema.vista.InscripcionCursoView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

// Controlador que conecta InscripcionCursoView con InscripcionCursoService y adapta permisos por rol
public class InscripcionCursoController implements ActionListener {

    private final InscripcionCursoView vista;
    private final InscripcionCursoService servicio;
    private final EstudianteDao estudianteDAO;
    private final GrupoDAO grupoDAO;
    private Usuario usuarioActual;
    private Integer idEstudianteFijo;
    private boolean estudianteSinVinculo;

    public InscripcionCursoController(InscripcionCursoView vista, InscripcionCursoDAO modeloDAO) {
        this(vista, new InscripcionCursoService(modeloDAO), null);
    }

    public InscripcionCursoController(InscripcionCursoView vista, InscripcionCursoService servicio) {
        this(vista, servicio, null);
    }

    public InscripcionCursoController(InscripcionCursoView vista, InscripcionCursoDAO modeloDAO, Usuario usuarioActual) {
        this(vista, new InscripcionCursoService(modeloDAO), usuarioActual);
    }

    public InscripcionCursoController(InscripcionCursoView vista, InscripcionCursoService servicio, Usuario usuarioActual) {
        this.vista = vista;
        this.servicio = servicio;
        this.usuarioActual = usuarioActual;
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
        if (usuarioActual == null || usuarioActual.getRol() == null
                || !java.util.Set.of("ADMIN", "DOCENTE", "ESTUDIANTE").contains(
                    usuarioActual.getRol().toUpperCase(java.util.Locale.ROOT))) {
            vista.mostrarError("Se requiere iniciar sesion para acceder a las inscripciones.");
            return;
        }
        aplicarRestriccionesPorRol();
        cargarCombos();
        listarInscripciones();
        vista.setVisible(true);
    }

    // Restringe acciones segun el rol para que Estudiante solo matricule y Docente solo califique
    private void aplicarRestriccionesPorRol() {
        if (usuarioActual == null || usuarioActual.getRol() == null) {
            return;
        }
        String rol = usuarioActual.getRol().toUpperCase();
        if (rol.equals("ESTUDIANTE")) {
            vista.getLblTituloBanner().setText("Matricula Academica de Asignaturas (Estudiante)");
            vista.getCmbEstudiante().setEnabled(false);
            vista.getTxtNotaFinal().setEnabled(false);
            vista.getCmbEstado().setEnabled(false);
            vista.getBtnActualizar().setVisible(false);
            vista.getBtnEliminar().setVisible(false);

        } else if (rol.equals("DOCENTE")) {
            vista.getLblTituloBanner().setText("Registro Docente de Calificaciones");
            vista.getCmbEstudiante().setEnabled(false);
            vista.getCmbGrupo().setEnabled(false);
            vista.getBtnMatricular().setVisible(false);
            vista.getBtnEliminar().setVisible(false);
        }
    }

    public void cargarCombos() {
        record Combos(List<String> estudiantes,List<String> grupos,Integer idEstudiante) { }
        TrabajoUI.ejecutar(vista, () -> {
            List<String> estudiantes=new java.util.ArrayList<>(), grupos=new java.util.ArrayList<>();
            Integer fijo=null;
            if ("ESTUDIANTE".equalsIgnoreCase(usuarioActual.getRol())) {
                Estudiante e=new IdentidadAcademicaService(estudianteDAO).exigirEstudiante(usuarioActual);
                fijo=e.getIdEstudiante();
                estudiantes.add(fijo+" - "+e.getNombre()+" "+e.getApellido());
                grupos=servicio.obtenerComboGrupos();
            } else if ("DOCENTE".equalsIgnoreCase(usuarioActual.getRol())) {
                java.util.Set<Integer> idsEst=new java.util.HashSet<>(),idsGrp=new java.util.HashSet<>();
                for (InscripcionCurso i:servicio.listarParaUsuario(usuarioActual)) {
                    if (idsEst.add(i.getIdEstudiante())) estudiantes.add(i.getIdEstudiante()+" - "+i.getNombreEstudiante());
                    if (idsGrp.add(i.getIdGrupo())) grupos.add(i.getIdGrupo()+" - "+i.getInfoGrupo());
                }
            } else { estudiantes=servicio.obtenerComboEstudiantes(); grupos=servicio.obtenerComboGrupos(); }
            return new Combos(estudiantes,grupos,fijo);
        }, datos -> {
            estudianteSinVinculo=false; idEstudianteFijo=datos.idEstudiante();
            if ("ESTUDIANTE".equalsIgnoreCase(usuarioActual.getRol())) vista.getBtnMatricular().setEnabled(true);
            vista.getCmbEstudiante().removeAllItems(); vista.getCmbGrupo().removeAllItems();
            datos.estudiantes().forEach(vista.getCmbEstudiante()::addItem);
            datos.grupos().forEach(vista.getCmbGrupo()::addItem);
        }, mensaje -> {
            estudianteSinVinculo=true; vista.getCmbEstudiante().removeAllItems(); vista.getCmbGrupo().removeAllItems();
            vista.getBtnMatricular().setEnabled(false);
            vista.getModeloTabla().setRowCount(0); vista.mostrarError(mensaje);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (TrabajoUI.ocupado(vista)) return;
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

        }
    }

    public void listarInscripciones() {
        vista.getModeloTabla().setRowCount(0);
        TrabajoUI.ejecutar(vista, () -> servicio.listarParaUsuario(usuarioActual), lista -> {
            if (estudianteSinVinculo) { vista.getBtnMatricular().setEnabled(false); return; }
            for (InscripcionCurso i:lista) vista.getModeloTabla().addRow(new Object[]{i.getIdInscripcion(),
                i.getIdEstudiante(),i.getNombreEstudiante(),i.getIdGrupo(),i.getInfoGrupo(),
                i.getNotaFinal()!=null?i.getNotaFinal():"Sin Calificar",i.getEstado()});
        },vista::mostrarError);
    }

    private void matricularEstudiante() {
        if (estudianteSinVinculo) {
            vista.mostrarError("La cuenta debe tener un estudiante asociado antes de matricular.");
            return;
        }
        String estSeleccionado = (String) vista.getCmbEstudiante().getSelectedItem();
        String grupoSeleccionado = (String) vista.getCmbGrupo().getSelectedItem();

        if (estSeleccionado == null || grupoSeleccionado == null) {
            vista.mostrarError("Debes seleccionar un Estudiante y un Grupo.");
            return;
        }

        int idEstudiante = extraerIdDeCombo(estSeleccionado);
        int idGrupo = extraerIdDeCombo(grupoSeleccionado);

        Double nota = null;
        if (vista.getTxtNotaFinal().isEnabled()) {
            nota = parsearNota(vista.getTxtNotaFinal().getText().trim());
            if (nota != null && nota == -1.0) {
                vista.mostrarError("La nota debe ser un numero valido (ej: 4.5).");
                return;
            }
        }

        String estado = (String) vista.getCmbEstado().getSelectedItem();

        try {
            InscripcionCurso nueva = new InscripcionCurso(idEstudiante, idGrupo, nota, estado);
            if (usuarioActual != null && "ESTUDIANTE".equalsIgnoreCase(usuarioActual.getRol())) nueva.setEstado("Matriculado");
            TrabajoUI.ejecutar(vista, () -> servicio.matricularEstudiante(nueva, usuarioActual), exito -> {
            if (exito) {
                vista.mostrarMensaje("Asignatura matriculada exitosamente.");
                vista.limpiarFormulario();
                listarInscripciones();
            } else {
                vista.mostrarError("No se pudo registrar la matricula.");
            }
            }, vista::mostrarError);
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void actualizarInscripcion() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona una fila de la tabla para guardar la calificacion.");
            return;
        }

        Double nota = parsearNota(vista.getTxtNotaFinal().getText().trim());
        if (nota != null && nota == -1.0) {
            vista.mostrarError("La nota debe ser un numero valido entre 0.0 y 5.0.");
            return;
        }

        try {
            int idInscripcion = Integer.parseInt(idStr);
            String estado = (String) vista.getCmbEstado().getSelectedItem();

            InscripcionCurso modificada = new InscripcionCurso();
            modificada.setIdInscripcion(idInscripcion);
            modificada.setNotaFinal(nota);
            modificada.setEstado(estado);

            TrabajoUI.ejecutar(vista, () -> servicio.actualizarCalificacion(modificada, usuarioActual), exito -> {
            if (exito) {
                vista.mostrarMensaje("Calificacion guardada exitosamente.");
                vista.limpiarFormulario();
                listarInscripciones();
            } else {
                vista.mostrarError("No se pudo actualizar la calificacion.");
            }
            }, vista::mostrarError);
        } catch (NumberFormatException e) {
            vista.mostrarError("ID de inscripcion no valido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void eliminarInscripcion() {
        String idStr = vista.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            vista.mostrarError("Selecciona una matricula de la tabla para cancelar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean confirmar = vista.confirmarAccion("Deseas cancelar la matricula con ID " + id + "?");
            if (confirmar) {
                TrabajoUI.ejecutar(vista, () -> servicio.eliminarInscripcion(id, usuarioActual), exito -> {
            if (exito) {
                    vista.mostrarMensaje("Matricula cancelada; su historial se conserva.");
                    vista.limpiarFormulario();
                    listarInscripciones();
                } else {
                    vista.mostrarError("La matricula no existe o ya estaba cancelada.");
                }
            }, vista::mostrarError);
            }
        } catch (NumberFormatException e) {
            vista.mostrarError("ID de inscripcion no valido.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
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
            return -1.0;
        }
    }
}
