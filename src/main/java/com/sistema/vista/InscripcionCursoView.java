package com.sistema.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista gráfica (Swing) para la Gestión de Matrículas y Calificaciones (Inscripcion_Curso).
 * Diseñada siguiendo el patrón Modelo-Vista-Controlador (MVC).
 */
public class InscripcionCursoView extends JFrame {

    // Componentes del Formulario
    private JTextField txtId;
    private JComboBox<String> cmbEstudiante;
    private JComboBox<String> cmbGrupo;
    private JTextField txtNotaFinal;
    private JComboBox<String> cmbEstado;

    // Botones de Acción
    private JButton btnMatricular;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRefrescar;

    // Tabla y Modelo
    private JTable tblInscripciones;
    private DefaultTableModel modeloTabla;

    public InscripcionCursoView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema Académico - Matrículas y Calificaciones");
        setSize(950, 600);
        setMinimumSize(new Dimension(850, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Principal con margen
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        // 1. TÍTULO SUPERIOR
        JLabel lblTitulo = new JLabel("📝 Gestión de Matrículas y Calificaciones", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 37, 41));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO: FORMULARIO Y BOTONES
        JPanel panelIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelIzquierdo.setPreferredSize(new Dimension(380, 0));
        panelIzquierdo.setOpaque(false);

        // Formulario de datos
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Datos de Matrícula",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(50, 70, 90)
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Fila 0: ID Inscripción
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("ID Inscripción:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(12);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelFormulario.add(txtId, gbc);

        // Fila 1: Estudiante
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Estudiante:"), gbc);
        gbc.gridx = 1;
        cmbEstudiante = new JComboBox<>();
        panelFormulario.add(cmbEstudiante, gbc);

        // Fila 2: Grupo
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Grupo / Materia:"), gbc);
        gbc.gridx = 1;
        cmbGrupo = new JComboBox<>();
        panelFormulario.add(cmbGrupo, gbc);

        // Fila 3: Nota Final (0.0 - 5.0)
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Nota Final (0.0-5.0):"), gbc);
        gbc.gridx = 1;
        txtNotaFinal = new JTextField(12);
        panelFormulario.add(txtNotaFinal, gbc);

        // Fila 4: Estado
        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        String[] estados = {"Matriculado", "Aprobado", "Reprobado", "Cancelado"};
        cmbEstado = new JComboBox<>(estados);
        panelFormulario.add(cmbEstado, gbc);

        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);

        // Panel de Botones
        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 8, 8));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnMatricular = new JButton("➕ Matricular Estudiante");
        btnActualizar = new JButton("📝 Calificar / Actualizar");
        btnEliminar = new JButton("🗑️ Cancelar Matrícula");
        btnLimpiar = new JButton("🧹 Limpiar Campos");
        btnRefrescar = new JButton("🔄 Recargar Lista");

        configurarBoton(btnMatricular);
        configurarBoton(btnActualizar);
        configurarBoton(btnEliminar);
        configurarBoton(btnLimpiar);
        configurarBoton(btnRefrescar);

        panelBotones.add(btnMatricular);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRefrescar);

        panelIzquierdo.add(panelBotones, BorderLayout.CENTER);
        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);

        // 3. PANEL DERECHO: TABLA DE MATRÍCULAS
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Listado de Matrículas y Notas",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(50, 70, 90)
                )
        ));

        String[] columnas = {"ID", "ID Est.", "Estudiante", "ID Grupo", "Grupo / Materia", "Nota", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblInscripciones = new JTable(modeloTabla);
        tblInscripciones.setRowHeight(24);
        tblInscripciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblInscripciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblInscripciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configuración de anchos de columna
        tblInscripciones.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblInscripciones.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblInscripciones.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblInscripciones.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblInscripciones.getColumnModel().getColumn(4).setPreferredWidth(180);
        tblInscripciones.getColumnModel().getColumn(5).setPreferredWidth(60);
        tblInscripciones.getColumnModel().getColumn(6).setPreferredWidth(90);

        JScrollPane scrollTabla = new JScrollPane(tblInscripciones);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);

        panelPrincipal.add(panelTabla, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private void configurarBoton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Getters
    public JTextField getTxtId() { return txtId; }
    public JComboBox<String> getCmbEstudiante() { return cmbEstudiante; }
    public JComboBox<String> getCmbGrupo() { return cmbGrupo; }
    public JTextField getTxtNotaFinal() { return txtNotaFinal; }
    public JComboBox<String> getCmbEstado() { return cmbEstado; }
    public JButton getBtnMatricular() { return btnMatricular; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnRefrescar() { return btnRefrescar; }
    public JTable getTblInscripciones() { return tblInscripciones; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarFormulario() {
        txtId.setText("");
        if (cmbEstudiante.getItemCount() > 0) cmbEstudiante.setSelectedIndex(0);
        if (cmbGrupo.getItemCount() > 0) cmbGrupo.setSelectedIndex(0);
        txtNotaFinal.setText("");
        cmbEstado.setSelectedItem("Matriculado");
        tblInscripciones.clearSelection();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmarAccion(String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }
}
