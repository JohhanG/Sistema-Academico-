package com.sistema.vista;

import com.sistema.controlador.DocenteController;
import com.sistema.controlador.EstudianteController;
import com.sistema.controlador.GrupoControlador;
import com.sistema.controlador.InscripcionCursoController;
import com.sistema.controlador.LoginController;
import com.sistema.controlador.MateriaController;
import com.sistema.controlador.UsuarioController;
import com.sistema.modelo.DocenteDAO;
import com.sistema.modelo.EstudianteDao;
import com.sistema.modelo.InscripcionCursoDAO;
import com.sistema.modelo.MateriaDAO;
import com.sistema.modelo.Usuario;
import com.sistema.servicio.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Ventana de menu principal adaptada segun el rol academico (Estudiante, Docente o Admin)
public class MenuPrincipalView extends JFrame implements ActionListener {

    private static final Color AZUL_OSCURO = new Color(11, 56, 97);
    private static final Color AMARILLO_UNIAJC = new Color(242, 183, 5);

    private final Usuario usuarioActual;
    private JPanel panelModulos;

    private JButton btnConsultarNotas;
    private JButton btnMatriculas;
    private JButton btnEstudiantes;
    private JButton btnDocentes;
    private JButton btnMaterias;
    private JButton btnGrupos;
    private JButton btnUsuarios;
    private JButton btnCerrarSesion;

    public MenuPrincipalView() {
        this(new Usuario(0, "admin", "", "ADMIN", null, null));
    }

    public MenuPrincipalView(Usuario usuarioActual) {
        if (usuarioActual != null) {
            this.usuarioActual = usuarioActual;
        } else {
            this.usuarioActual = new Usuario(0, "admin", "", "ADMIN", null, null);
        }
        initComponents();
        configurarMenuSegunRol();
        inicializarEventos();
    }

    private void initComponents() {
        setTitle("Portal Academico UNIAJC - Menu Principal");
        setSize(780, 520);
        setMinimumSize(new Dimension(680, 440));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 15));
        panelPrincipal.setBackground(Color.WHITE);

        // Encabezado superior estilo portal universitario
        JPanel panelBanner = new JPanel(new BorderLayout());
        JPanel barraAzul = new JPanel(new BorderLayout());
        barraAzul.setBackground(AZUL_OSCURO);
        barraAzul.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel("SISTEMA DE INFORMACION ACADEMICA - UNIAJC");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel(
                "Usuario: " + usuarioActual.getUsername().toUpperCase() + "  |  Perfil: " + usuarioActual.getRol(),
                SwingConstants.RIGHT
        );
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsuario.setForeground(AMARILLO_UNIAJC);

        barraAzul.add(lblTitulo, BorderLayout.WEST);
        barraAzul.add(lblUsuario, BorderLayout.EAST);

        JPanel barraAmarilla = new JPanel(new BorderLayout());
        barraAmarilla.setBackground(AMARILLO_UNIAJC);
        barraAmarilla.setPreferredSize(new Dimension(0, 26));
        JLabel lblSeccion = new JLabel("   Seleccione un servicio academico disponible para su perfil:");
        lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        barraAmarilla.add(lblSeccion, BorderLayout.CENTER);

        panelBanner.add(barraAzul, BorderLayout.CENTER);
        panelBanner.add(barraAmarilla, BorderLayout.SOUTH);
        panelPrincipal.add(panelBanner, BorderLayout.NORTH);

        // Panel central donde se agregan solo las opciones permitidas para el rol
        panelModulos = new JPanel(new GridLayout(0, 2, 16, 16));
        panelModulos.setBackground(Color.WHITE);
        panelModulos.setBorder(new EmptyBorder(15, 30, 15, 30));

        btnConsultarNotas = crearBotonServicio("Consultar Notas Actuales (Boletin)");
        btnMatriculas = crearBotonServicio("Matriculas y Calificaciones");
        btnEstudiantes = crearBotonServicio("Admisiones y Registro de Estudiantes");
        btnDocentes = crearBotonServicio("Planta Docente");
        btnMaterias = crearBotonServicio("Plan de Estudios (Materias)");
        btnGrupos = crearBotonServicio("Programacion de Grupos y Horarios");
        btnUsuarios = crearBotonServicio("Control de Usuarios y Roles");

        panelPrincipal.add(panelModulos, BorderLayout.CENTER);

        // Pie de pagina
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 12));
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(new LineBorder(new Color(220, 224, 230), 1));

        btnCerrarSesion = new JButton("Cerrar Sesion");
        btnCerrarSesion.setBackground(AZUL_OSCURO);
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrarSesion.setOpaque(true);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setFocusPainted(false);
        panelFooter.add(btnCerrarSesion);

        panelPrincipal.add(panelFooter, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private JButton crearBotonServicio(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(248, 249, 250));
        btn.setForeground(AZUL_OSCURO);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(180, 195, 210), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Muestra unicamente los modulos que corresponden al rol del usuario
    private void configurarMenuSegunRol() {
        panelModulos.removeAll();
        String rol = usuarioActual.getRol() != null ? usuarioActual.getRol().toUpperCase() : "ADMIN";

        if (rol.equals("ESTUDIANTE")) {
            btnConsultarNotas.setText("Consultar Notas Actuales");
            btnMatriculas.setText("Matricular Asignaturas");
            panelModulos.add(btnConsultarNotas);
            panelModulos.add(btnMatriculas);
        } else if (rol.equals("DOCENTE")) {
            btnMatriculas.setText("Registrar Calificaciones de mis Grupos");
            btnConsultarNotas.setText("Consultar Boletin por Estudiante");
            panelModulos.add(btnMatriculas);
            panelModulos.add(btnConsultarNotas);
        } else {
            // Rol ADMIN ve todos los modulos academicos
            panelModulos.add(btnConsultarNotas);
            panelModulos.add(btnMatriculas);
            panelModulos.add(btnEstudiantes);
            panelModulos.add(btnDocentes);
            panelModulos.add(btnMaterias);
            panelModulos.add(btnGrupos);
            panelModulos.add(btnUsuarios);
        }
    }

    private void inicializarEventos() {
        btnConsultarNotas.addActionListener(this);
        btnMatriculas.addActionListener(this);
        btnEstudiantes.addActionListener(this);
        btnDocentes.addActionListener(this);
        btnMaterias.addActionListener(this);
        btnGrupos.addActionListener(this);
        btnUsuarios.addActionListener(this);
        btnCerrarSesion.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnConsultarNotas) {
            setVisible(false);
            ConsultaNotasView vista = new ConsultaNotasView(usuarioActual, this);
            vista.setVisible(true);
        } else if (source == btnMatriculas) {
            setVisible(false);
            InscripcionCursoView vista = new InscripcionCursoView();
            vista.setMenuPrincipal(this);
            InscripcionCursoController ctrl = new InscripcionCursoController(vista, new InscripcionCursoDAO(), usuarioActual);
            ctrl.iniciar();
        } else if (source == btnEstudiantes) {
            setVisible(false);
            EstudianteView vista = new EstudianteView();
            vista.setMenuPrincipal(this);
            EstudianteController ctrl = new EstudianteController(vista, new EstudianteDao());
            ctrl.iniciar();
        } else if (source == btnDocentes) {
            setVisible(false);
            DocenteView vista = new DocenteView();
            vista.setMenuPrincipal(this);
            DocenteController ctrl = new DocenteController(vista, new DocenteDAO());
            ctrl.iniciar();
        } else if (source == btnMaterias) {
            setVisible(false);
            MateriaView vista = new MateriaView();
            vista.setMenuPrincipal(this);
            MateriaController ctrl = new MateriaController(vista, new MateriaDAO());
            ctrl.iniciar();
        } else if (source == btnGrupos) {
            setVisible(false);
            GrupoView vista = new GrupoView();
            vista.setMenuPrincipal(this);
            GrupoControlador ctrl = new GrupoControlador(vista);
            ctrl.iniciar();
        } else if (source == btnUsuarios) {
            setVisible(false);
            UsuarioView vista = new UsuarioView();
            vista.setMenuPrincipal(this);
            UsuarioController ctrl = new UsuarioController(vista, new UsuarioService());
            ctrl.iniciar();
        } else if (source == btnCerrarSesion) {
            dispose();
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController(loginView, new UsuarioService());
            loginController.iniciar();
        }
    }
}
