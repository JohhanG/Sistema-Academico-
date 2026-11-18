package com.sistema.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Vista Swing para la gestion de materias del pensum
public class MateriaView extends JFrame {

    private JFrame menuPrincipal;

    private JTextField txtId;
    private JTextField txtNombreMateria;
    private JSpinner spnCreditos;

    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRefrescar;
    private JButton btnVolverMenu;

    private JTable tblMaterias;
    private DefaultTableModel modeloTabla;

    public MateriaView() {
        initComponents();
    }

    public void setMenuPrincipal(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }

    private void initComponents() {
        setTitle("Sistema Academico - Plan de Estudios (Materias)");
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
        JLabel lblTitulo = new JLabel("Plan de Estudios - Asignaturas y Creditos", SwingConstants.CENTER);
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
        panelIzquierdo.setOpaque(false);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Datos de la Asignatura",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(11, 56, 97)
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Codigo Materia:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelFormulario.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Nombre Asignatura:"), gbc);
        gbc.gridx = 1;
        txtNombreMateria = new JTextField(15);
        panelFormulario.add(txtNombreMateria, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Creditos Academicos:"), gbc);
        gbc.gridx = 1;
        spnCreditos = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1));
        panelFormulario.add(spnCreditos, gbc);

        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(6, 1, 8, 8));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnGuardar = new JButton("Registrar Materia");
        btnActualizar = new JButton("Actualizar Materia");
        btnEliminar = new JButton("Eliminar Materia");
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
                        "Pensum de Materias",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(11, 56, 97)
                )
        ));

        String[] columnas = {"Codigo ID", "Descripcion (Materia)", "Creditos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMaterias = new JTable(modeloTabla);
        tblMaterias.setRowHeight(24);
        tblMaterias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMaterias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblMaterias.getTableHeader().setBackground(new Color(206, 212, 218));
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
    public JTextField getTxtNombreMateria() { return txtNombreMateria; }
    public JSpinner getSpnCreditos() { return spnCreditos; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnRefrescar() { return btnRefrescar; }
    public JButton getBtnVolverMenu() { return btnVolverMenu; }
    public JTable getTblMaterias() { return tblMaterias; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarFormulario() {
        txtId.setText("");
        txtNombreMateria.setText("");
        spnCreditos.setValue(3);
        tblMaterias.clearSelection();
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
