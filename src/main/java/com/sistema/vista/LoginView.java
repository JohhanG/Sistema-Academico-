package com.sistema.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

// Ventana de inicio de sesion con diseno institucional UNIAJC
public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;

    public LoginView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Portal Academico UNIAJC - Inicio de Sesion");
        setSize(440, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 15));
        panelPrincipal.setBackground(Color.WHITE);

        // Encabezado azul y amarillo
        JPanel panelBanner = new JPanel(new BorderLayout());
        JPanel barraAzul = new JPanel(new GridLayout(2, 1));
        barraAzul.setBackground(new Color(11, 56, 97));
        barraAzul.setBorder(new EmptyBorder(14, 15, 14, 15));

        JLabel lblTitulo = new JLabel("PORTAL ACADEMICO UNIAJC", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Ingreso al Sistema de Informacion Academica", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(220, 230, 242));

        barraAzul.add(lblTitulo);
        barraAzul.add(lblSubtitulo);

        JPanel barraAmarilla = new JPanel();
        barraAmarilla.setBackground(new Color(242, 183, 5));
        barraAmarilla.setPreferredSize(new Dimension(0, 6));

        panelBanner.add(barraAzul, BorderLayout.CENTER);
        panelBanner.add(barraAmarilla, BorderLayout.SOUTH);
        panelPrincipal.add(panelBanner, BorderLayout.NORTH);

        // Formulario central
        JPanel contenedorCentro = new JPanel(new BorderLayout());
        contenedorCentro.setBackground(Color.WHITE);
        contenedorCentro.setBorder(new EmptyBorder(5, 35, 5, 35));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(new Color(248, 249, 250));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 215, 220), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Usuario Institucional:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFormulario.add(lblUser, gbc);

        gbc.gridy = 1;
        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelFormulario.add(txtUsername, gbc);

        gbc.gridy = 2;
        JLabel lblPass = new JLabel("Clave de Acceso:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFormulario.add(lblPass, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelFormulario.add(txtPassword, gbc);

        gbc.gridy = 4;
        JLabel lblRoles = new JLabel("Perfiles: admin (Admin) | cgomez (Docente) | johhang (Estudiante)");
        lblRoles.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRoles.setForeground(new Color(100, 110, 120));
        panelFormulario.add(lblRoles, gbc);

        contenedorCentro.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(contenedorCentro, BorderLayout.CENTER);

        // Botones inferiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        panelBotones.setBackground(Color.WHITE);

        btnIngresar = new JButton("Ingresar al Portal");
        btnIngresar.setBackground(new Color(11, 56, 97));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnIngresar.setOpaque(true);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setPreferredSize(new Dimension(160, 34));

        btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalir.setFocusPainted(false);
        btnSalir.setPreferredSize(new Dimension(110, 34));

        panelBotones.add(btnIngresar);
        panelBotones.add(btnSalir);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnIngresar);
        setContentPane(panelPrincipal);
    }

    public JTextField getTxtUsername() { return txtUsername; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnIngresar() { return btnIngresar; }
    public JButton getBtnSalir() { return btnSalir; }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Portal Academico", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Acceso", JOptionPane.ERROR_MESSAGE);
    }
}
