package com.sistema.soporte;

import com.sistema.controlador.UsuarioController;
import com.sistema.modelo.Usuario;
import com.sistema.servicio.UsuarioService;
import com.sistema.vista.UsuarioView;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Vista previa interactiva con datos descartables en memoria. No utiliza Supabase. */
public final class VistaPreviaUsuarios {
    public static void main(String[] args) throws Exception {
        BaseAcademicaTemporal base = new BaseAcademicaTemporal();
        UsuarioService servicio = new UsuarioService();
        servicio.registrarUsuario(new Usuario(0,"ana-prueba","clave-prueba","ESTUDIANTE",1,null));
        servicio.registrarUsuario(new Usuario(0,"docente-prueba","clave-prueba","DOCENTE",null,1));
        SwingUtilities.invokeLater(() -> {
            UsuarioView vista = new UsuarioView();
            vista.setDefaultCloseOperation(UsuarioView.DISPOSE_ON_CLOSE);
            vista.setTitle("Vista previa con datos de prueba - Usuarios y roles");
            vista.addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(WindowEvent e) {
                    try { base.close(); } catch (Exception ex) { ex.printStackTrace(); }
                }
            });
            new UsuarioController(vista, servicio).iniciar();
        });
    }
}
