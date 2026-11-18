package com.sistema.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Vista Swing para la gestion de matriculas y registro de calificaciones
public class InscripcionCursoView extends JFrame {

    private JFrame menuPrincipal;

    private JLabel lblTituloBanner;
    private JTextField txtId;
    private JComboBox<String> cmbEstudiante;
    private JComboBox<String> cmbGrupo;
    private JTextField txtNotaFinal;
    private JComboBox<String> cmbEstado;

    private JButton btnMatricular;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRefrescar;
    private JButton btnVolverMenu;

    private JTable tblInscripciones;
    private DefaultTableModel modeloTabla;

    public InscripcionCursoView() {
        initComponents();
    }

    public void setMenuPrincipal(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }

    private void initComponents() {
        setTitle("Sistema Academico - Matriculas y Calificaciones");
        setSize(960, 620);
        setMinimumSize(new Dimension(860, 520));
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
        lblTituloBanner = new JLabel("Gestion de Matriculas y Calificaciones", SwingConstants.CENTER);
        lblTituloBanner.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloBanner.setForeground(Color.WHITE);
        barraAzul.add(lblTituloBanner, BorderLayout.CENTER);

        JPanel barraAmarilla = new JPanel();
        barraAmarilla.setBackground(new Color(242, 183, 5));
        barraAmarilla.setPreferredSize(new Dimension(0, 6));

        panelBanner.add(barraAzul, BorderLayout.CENTER);
        panelBanner.add(barraAmarilla, BorderLayout.SOUTH);
        panelPrincipal.add(panelBanner, BorderLayout.NORTH);

        JPanel panelIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelIzquierdo.setPreferredSize(new Dimension(380, 0));
        panelIzquierdo.setOpaque(false);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Datos de Inscripcion y Nota",
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
        panelFormulario.add(new JLabel("Folio / ID:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(12);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelFormulario.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Estudiante:"), gbc);
        gbc.gridx = 1;
        cmbEstudiante = new JComboBox<>();
        panelFormulario.add(cmbEstudiante, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Grupo / Asignatura:"), gbc);
        gbc.gridx = 1;
        cmbGrupo = new JComboBox<>();
        panelFormulario.add(cmbGrupo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Nota Definitiva (0.0-5.0):"), gbc);
        gbc.gridx = 1;
        txtNotaFinal = new JTextField(12);
        panelFormulario.add(txtNotaFinal, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(new JLabel("Estado Academico:"), gbc);
        gbc.gridx = 1;
        String[] estados = {"Matriculado", "Aprobado", "Reprobado", "Cancelado"};
        cmbEstado = new JComboBox<>(estados);
        panelFormulario.add(cmbEstado, gbc);

        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(6, 1, 8, 8));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnMatricular = new JButton("Matricular Asignatura");
        btnActualizar = new JButton("Guardar Calificacion");
        btnEliminar = new JButton("Cancelar Matricula");
        btnLimpiar = new JButton("Limpiar Seleccion");
        btnRefrescar = new JButton("Recargar Listado");
        btnVolverMenu = new JButton("Volver al Menu Principal");

        configurarBoton(btnMatricular);
        configurarBoton(btnActualizar);
        configurarBoton(btnEliminar);
        configurarBoton(btnLimpiar);
        configurarBoton(btnRefrescar);
        configurarBotonVolver(btnVolverMenu);

        btnVolverMenu.addActionListener(e -> volverAlMenu());

        panelBotones.add(btnMatricular);
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
                        "Asignaturas Matriculadas y Notas",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(11, 56, 97)
                )
        ));

        String[] columnas = {"Folio", "ID Est.", "Estudiante", "ID Grupo", "Asignatura / Salon", "Nota", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblInscripciones = new JTable(modeloTabla);
        tblInscripciones.setRowHeight(24);
        tblInscripciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblInscripciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblInscripciones.getTableHeader().setBackground(new Color(206, 212, 218));
        tblInscripciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblInscripciones.getColumnModel().getColumn(0).setPreferredWidth(45);
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

    public JLabel getLblTituloBanner() { return lblTituloBanner; }
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
    public JButton getBtnVolverMenu() { return btnVolverMenu; }
    public JTable getTblInscripciones() { return tblInscripciones; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    public void limpiarFormulario() {
        txtId.setText("");
        if (cmbEstudiante.isEnabled() && cmbEstudiante.getItemCount() > 0) {
            cmbEstudiante.setSelectedIndex(0);
        }
        if (cmbGrupo.isEnabled() && cmbGrupo.getItemCount() > 0) {
            cmbGrupo.setSelectedIndex(0);
        }
        txtNotaFinal.setText("");
        cmbEstado.setSelectedItem("Matriculado");
        tblInscripciones.clearSelection();
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
