package com.sistema;

import com.sistema.controlador.EstudianteController;
import com.sistema.modelo.EstudianteDAO;
import com.sistema.vista.EstudianteView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada principal de la aplicación Sistema Académico.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar Look and Feel nativo del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
            }

            // Instanciación de componentes MVC
            EstudianteDAO modelo = new EstudianteDAO();
            EstudianteView vista = new EstudianteView();
            EstudianteController controlador = new EstudianteController(vista, modelo);

            // Iniciar la aplicación
            controlador.iniciar();
        });
    }
}

