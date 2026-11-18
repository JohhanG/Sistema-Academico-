package com.sistema.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista gráfica (Swing) para el módulo de Gestión de Materias.
 * Diseñada siguiendo el patrón Modelo-Vista-Controlador (MVC).
 */
public class MateriaView extends JFrame {

    // Componentes del Formulario
    private JTextField txtId;
    private JTextField txtNombreMateria;
    private JSpinner spnCreditos;

    // Botones de Acción
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRefrescar;

    // Tabla y Modelo
    private JTable tblMaterias;
    private DefaultTableModel modeloTabla;

    public MateriaView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema Académico - Gestión de Materias");
        setSize(800, 550);
        setMinimumSize(new Dimension(700, 480));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Principal con margen
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        // 1. TÍTULO SUPERIOR
        JLabel lblTitulo = new JLabel("📚 Gestión de Materias", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 37, 41));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO: FORMULARIO Y BOTONES
        JPanel panelIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelIzquierdo.setOpaque(false);

        // Formulario de datos
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Datos de la Materia",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(50, 70, 90)
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Fila 0: ID (Solo lectura)
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("ID Materia:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelFormulario.add(txtId, gbc);

        // Fila 1: Nombre de la Materia
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Nombre Materia:"), gbc);
        gbc.gridx = 1;
        txtNombreMateria = new JTextField(15);
        panelFormulario.add(txtNombreMateria, gbc);

        // Fila 2: Créditos (Mínimo 1 crédito)
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Créditos:"), gbc);
        gbc.gridx = 1;
        spnCreditos = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1));
        panelFormulario.add(spnCreditos, gbc);

        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);

        // Panel de Botones
        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 8, 8));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnGuardar = new JButton("➕ Registrar");
        btnActualizar = new JButton("✏️ Actualizar");
        btnEliminar = new JButton("🗑️ Eliminar");
        btnLimpiar = new JButton("🧹 Limpiar Campos");
        btnRefrescar = new JButton("🔄 Recargar Lista");

        configurarBoton(btnGuardar);
        configurarBoton(btnActualizar);
        configurarBoton(btnEliminar);
        configurarBoton(btnLimpiar);
        configurarBoton(btnRefrescar);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRefrescar);

        panelIzquierdo.add(panelBotones, BorderLayout.CENTER);
        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);

        // 3. PANEL DERECHO: TABLA DE MATERIAS
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Listado de Materias Registradas",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(50, 70, 90)
                )
        ));

        String[] columnas = {"ID", "Nombre Materia", "Créditos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMaterias = new JTable(modeloTabla);
        tblMaterias.setRowHeight(24);
        tblMaterias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMaterias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblMaterias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTabla = new JScrollPane(tblMaterias);
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
    public JTextField getTxtNombreMateria() { return txtNombreMateria; }
    public JSpinner getSpnCreditos() { return spnCreditos; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnRefrescar() { return btnRefrescar; }
    public JTable getTblMaterias() { return tblMaterias; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarFormulario() {
        txtId.setText("");
        txtNombreMateria.setText("");
        spnCreditos.setValue(3);
        tblMaterias.clearSelection();
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
