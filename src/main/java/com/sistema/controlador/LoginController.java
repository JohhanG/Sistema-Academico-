package com.sistema.controlador;

import com.sistema.modelo.Usuario;
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

        try {
            Usuario usuarioAutenticado = servicio.autenticar(username, password);
            if (usuarioAutenticado != null) {
                vista.mostrarMensaje("Bienvenido " + usuarioAutenticado.getUsername()
                        + " (Rol: " + usuarioAutenticado.getRol() + ")");
                vista.dispose();
                MenuPrincipalView menu = new MenuPrincipalView(usuarioAutenticado);
                menu.setVisible(true);
            } else {
                vista.mostrarError("Usuario o contrasena incorrectos.");
            }
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (Exception ex) {
            vista.mostrarError("Error al iniciar sesion: " + ex.getMessage());
        }
    }
}
