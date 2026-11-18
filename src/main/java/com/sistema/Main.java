package com.sistema;

import com.sistema.controlador.LoginController;
import com.sistema.servicio.UsuarioService;
import com.sistema.vista.LoginView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Clase principal que inicia el sistema en la ventana de Login
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("No se pudo aplicar el estilo visual: " + e.getMessage());
                }

                LoginView loginView = new LoginView();
                UsuarioService usuarioService = new UsuarioService();
                LoginController loginController = new LoginController(loginView, usuarioService);
                loginController.iniciar();
            }
        });
    }
}
