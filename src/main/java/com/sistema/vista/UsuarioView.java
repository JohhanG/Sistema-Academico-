package com.sistema.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Vista Swing para la gestion de cuentas de usuario y roles
public class UsuarioView extends JFrame {

    private JFrame menuPrincipal;

    private JTextField txtId;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JComboBox<String> cmbEstudiante;
    private JComboBox<String> cmbDocente;

    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRefrescar;
    private JButton btnVolverMenu;

    private JTable tblUsuarios;
    private DefaultTableModel modeloTabla;

    public UsuarioView() {
        initComponents();
    }

    public void setMenuPrincipal(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }

    private void initComponents() {
        setTitle("Sistema Academico - Control de Accesos y Roles");
        setSize(850, 580);
        setMinimumSize(new Dimension(750, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volverAlMenu();
            }
        });

        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBorder(new EmptyBorder(12, 15, 12, 15));
        panelPrincipal.setBackground(Color.WHITE);

        JPanel panelBanner = new JPanel(new BorderLayout());
        JPanel barraAzul = new JPanel(new BorderLayout());
        barraAzul.setBackground(new Color(11, 56, 97));
        barraAzul.setPreferredSize(new Dimension(0, 36));
        JLabel lblTitulo = new JLabel("Control de Usuarios y Roles Academicos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        barraAzul.add(lblTitulo, BorderLayout.CENTER);

        JPanel barraAmarilla = new JPanel();
        barraAmarilla.setBackground(new Color(242, 183, 5));
        barraAmarilla.setPreferredSize(new Dimension(0, 6));

        panelBanner.add(barraAzul, BorderLayout.CENTER);
        panelBanner.add(barraAmarilla, BorderLayout.SOUTH);
        panelPrincipal.add(panelBanner, BorderLayout.NORTH);

        JPanel panelIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelIzquierdo.setPreferredSize(new Dimension(340, 0));
        panelIzquierdo.setOpaque(false);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Cuenta de Acceso",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(11, 56, 97)
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("ID Usuario:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(12);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelFormulario.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(14);
        panelFormulario.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Contrasena:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(14);
        panelFormulario.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Rol Academico:"), gbc);
        gbc.gridx = 1;
        cmbRol = new JComboBox<>(new String[]{"ADMIN", "DOCENTE", "ESTUDIANTE"});
        panelFormulario.add(cmbRol, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelFormulario.add(new JLabel("Estudiante:"), gbc);
        gbc.gridx = 1;
        cmbEstudiante = new JComboBox<>();
        panelFormulario.add(cmbEstudiante, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panelFormulario.add(new JLabel("Docente:"), gbc);
        gbc.gridx = 1;
        cmbDocente = new JComboBox<>();
        panelFormulario.add(cmbDocente, gbc);

        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(6, 1, 8, 8));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnGuardar = new JButton("Crear Usuario");
        btnActualizar = new JButton("Actualizar Usuario");
        btnEliminar = new JButton("Eliminar Usuario");
        btnLimpiar = new JButton("Limpiar Campos");
        btnRefrescar = new JButton("Recargar Listado");
        btnVolverMenu = new JButton("Volver al Menu Principal");

        configurarBoton(btnGuardar);
        configurarBoton(btnActualizar);
        configurarBoton(btnEliminar);
        configurarBoton(btnLimpiar);
        configurarBoton(btnRefrescar);
        configurarBotonVolver(btnVolverMenu);

        btnVolverMenu.addActionListener(e -> volverAlMenu());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnVolverMenu);

        panelIzquierdo.add(panelBotones, BorderLayout.CENTER);
        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Cuentas Registradas",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(11, 56, 97)
                )
        ));

        String[] columnas = {"ID", "Username", "Rol", "Hash Contrasena"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblUsuarios = new JTable(modeloTabla);
        tblUsuarios.setRowHeight(24);
        tblUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblUsuarios.getTableHeader().setBackground(new Color(206, 212, 218));
        tblUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTabla = new JScrollPane(tblUsuarios);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);

        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
        setContentPane(panelPrincipal);
    }

    private void configurarBoton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void configurarBotonVolver(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(11, 56, 97));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void volverAlMenu() {
        dispose();
        if (menuPrincipal != null) {
            menuPrincipal.setVisible(true);
        }
    }

    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtUsername() { return txtUsername; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JComboBox<String> getCmbRol() { return cmbRol; }
    public JComboBox<String> getCmbEstudiante() {return cmbEstudiante;}
    public JComboBox<String> getCmbDocente() {return cmbDocente;}
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnRefrescar() { return btnRefrescar; }
    public JButton getBtnVolverMenu() { return btnVolverMenu; }
    public JTable getTblUsuarios() { return tblUsuarios; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarFormulario() {
        txtId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        tblUsuarios.clearSelection();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmarAccion(String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmacion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }
}
