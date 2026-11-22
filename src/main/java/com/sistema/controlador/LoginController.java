package com.sistema.controlador;

import com.sistema.modelo.Usuario;
import com.sistema.vista.TrabajoUI;
import com.sistema.modelo.UsuarioDAO;
import com.sistema.servicio.UsuarioService;
import com.sistema.vista.LoginView;
import com.sistema.vista.MenuPrincipalView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Controlador para la pantalla de Login
public class LoginController implements ActionListener {

    private final LoginView vista;
    private final UsuarioService servicio;

    public LoginController(LoginView vista) {
        this(vista, new UsuarioService());
    }

    public LoginController(LoginView vista, UsuarioDAO dao) {
        this(vista, new UsuarioService(dao));
    }

    public LoginController(LoginView vista, UsuarioService servicio) {
        this.vista = vista;
        this.servicio = servicio;
        inicializarEventos();
    }

    private void inicializarEventos() {
        if (this.vista == null) return;
        this.vista.getBtnIngresar().addActionListener(this);
        this.vista.getBtnSalir().addActionListener(this);
    }

    public void iniciar() {
        if (this.vista != null) {
            this.vista.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (TrabajoUI.ocupado(vista)) return;
        Object source = e.getSource();
        if (source == vista.getBtnIngresar()) {
            procesarLogin();
        } else if (source == vista.getBtnSalir()) {
            System.exit(0);
        }
    }

    private void procesarLogin() {
        String username = vista.getTxtUsername().getText().trim();
        String password = new String(vista.getTxtPassword().getPassword());

        TrabajoUI.ejecutar(vista, () -> servicio.autenticar(username,password), usuarioAutenticado -> {
            if (usuarioAutenticado == null) { vista.mostrarError("Usuario o contrasena incorrectos."); return; }
            vista.dispose();
            new MenuPrincipalView(usuarioAutenticado).setVisible(true);
        }, vista::mostrarError);
    }
}
