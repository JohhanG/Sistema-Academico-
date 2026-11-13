package com.sistema.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista gráfica (Swing) para el módulo de Gestión de Estudiantes.
 * Diseñada siguiendo el patrón Modelo-Vista-Controlador (MVC).
 */
public class EstudianteView extends JFrame {

    // Componentes del Formulario
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;

    // Botones de Acción
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRefrescar;

    // Tabla y Modelo
    private JTable tblEstudiantes;
    private DefaultTableModel modeloTabla;

    public EstudianteView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema Académico - Gestión de Estudiantes");
        setSize(850, 600);
        setMinimumSize(new Dimension(750, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Principal con margen
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        // 1. TÍTULO SUPERIOR
        JLabel lblTitulo = new JLabel("🎓 Gestión de Estudiantes", SwingConstants.CENTER);
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
                        "Datos del Estudiante",
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

        // Fila 0: ID (Solo lectura)
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("ID Estudiante:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelFormulario.add(txtId, gbc);

        // Fila 1: Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);

        // Fila 2: Apellido
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        txtApellido = new JTextField(15);
        panelFormulario.add(txtApellido, gbc);

        // Fila 3: Email
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        panelFormulario.add(txtEmail, gbc);

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

        // Estilos de botones
        configurarBoton(btnGuardar, new Color(40, 167, 69));
        configurarBoton(btnActualizar, new Color(0, 123, 255));
        configurarBoton(btnEliminar, new Color(220, 53, 69));
        configurarBoton(btnLimpiar, new Color(108, 117, 125));
        configurarBoton(btnRefrescar, new Color(23, 162, 184));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRefrescar);

        panelIzquierdo.add(panelBotones, BorderLayout.CENTER);
        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);

        // 3. PANEL DERECHO / CENTRO: TABLA DE ESTUDIANTES
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Listado de Estudiantes Registrados",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(50, 70, 90)
                )
        ));

        String[] columnas = {"ID", "Nombre", "Apellido", "Correo Electrónico"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer celdas no editables directamente
            }
        };

        tblEstudiantes = new JTable(modeloTabla);
        tblEstudiantes.setRowHeight(24);
        tblEstudiantes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblEstudiantes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTabla = new JScrollPane(tblEstudiantes);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);

        panelPrincipal.add(panelTabla, BorderLayout.CENTER);

        // Asignar panel principal a la ventana
        setContentPane(panelPrincipal);
    }

    private void configurarBoton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // =========================================================================
    // MÉTODOS DE ACCESO A COMPONENTES (GETTERS)
    // =========================================================================

    public JTextField getTxtId() {
        return txtId;
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtApellido() {
        return txtApellido;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JButton getBtnRefrescar() {
        return btnRefrescar;
    }

    public JTable getTblEstudiantes() {
        return tblEstudiantes;
    }

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    // =========================================================================
    // MÉTODOS DE UTILIDAD PARA LA VISTA
    // =========================================================================

    public void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        tblEstudiantes.clearSelection();
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

