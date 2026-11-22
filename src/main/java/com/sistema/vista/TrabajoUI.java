package com.sistema.vista;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.swing.*;

/** Ejecuta JDBC fuera del hilo de Swing y publica resultados en el EDT. */
public final class TrabajoUI {
    private static final ExecutorService JDBC = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r,"sistema-jdbc"); t.setDaemon(true); return t;
    });
    private static final Map<Component,Integer> pendientes = new WeakHashMap<>();
    private static final Map<Component,Map<JButton,Boolean>> botones = new WeakHashMap<>();
    private static boolean sincronoParaPruebas;
    private TrabajoUI() { }

    public static boolean ocupado(Component ventana) { return pendientes.getOrDefault(ventana,0)>0; }

    public static <T> void ejecutar(Component ventana, Callable<T> consulta,
            Consumer<T> resultado, Consumer<String> error) {
        if (sincronoParaPruebas) {
            try { resultado.accept(consulta.call()); }
            catch (Exception e) { error.accept(mensaje(e)); }
            return;
        }
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> ejecutar(ventana,consulta,resultado,error)); return;
        }
        if (!ocupado(ventana)) {
            Map<JButton,Boolean> estado=new IdentityHashMap<>();
            guardarBotones(ventana,estado);
            botones.put(ventana,estado);
            ventana.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        pendientes.merge(ventana,1,Integer::sum);
        JDBC.submit(() -> {
            T valor=null; Exception fallo=null;
            try { valor=consulta.call(); } catch (Exception e) { fallo=e; }
            T finalValor=valor; Exception finalFallo=fallo;
            SwingUtilities.invokeLater(() -> {
                int restantes=pendientes.getOrDefault(ventana,1)-1;
                if (restantes==0) {
                    pendientes.remove(ventana);
                    Map<JButton,Boolean> estado=botones.remove(ventana);
                    if (estado!=null) estado.forEach(JButton::setEnabled);
                    ventana.setCursor(Cursor.getDefaultCursor());
                } else pendientes.put(ventana,restantes);
                if (!ventana.isDisplayable()) return;
                if (finalFallo==null) {
                    try { resultado.accept(finalValor); } catch (RuntimeException e) { error.accept(mensaje(e)); }
                } else error.accept(mensaje(finalFallo));
            });
        });
    }

    private static void guardarBotones(Component c,Map<JButton,Boolean> estado) {
        if (c instanceof JButton b) { estado.put(b,b.isEnabled()); b.setEnabled(false); }
        if (c instanceof Container contenedor) for (Component hijo:contenedor.getComponents()) guardarBotones(hijo,estado);
    }
    private static String mensaje(Exception e) {
        return e.getMessage()==null ? "No se pudo completar la operacion." : e.getMessage();
    }
}
