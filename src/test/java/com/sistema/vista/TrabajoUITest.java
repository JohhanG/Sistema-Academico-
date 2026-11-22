package com.sistema.vista;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrabajoUITest {
    @Test void consultaNoBloqueaSwingYPublicaResultadoEnSuHilo() throws Exception {
        CountDownLatch iniciada=new CountDownLatch(1), liberar=new CountDownLatch(1), terminada=new CountDownLatch(1);
        AtomicBoolean consultaFuera=new AtomicBoolean(), resultadoEnSwing=new AtomicBoolean();
        AtomicBoolean restaurado=new AtomicBoolean();
        JFrame[] ventana=new JFrame[1]; JButton[] boton=new JButton[1];
        try {
            SwingUtilities.invokeAndWait(()->{
                ventana[0]=new JFrame(); boton[0]=new JButton("Guardar"); ventana[0].add(boton[0]); ventana[0].pack();
                TrabajoUI.ejecutar(ventana[0],()->{
                    consultaFuera.set(!SwingUtilities.isEventDispatchThread()); iniciada.countDown();
                    if(!liberar.await(10,TimeUnit.SECONDS))throw new IllegalStateException("Tiempo agotado");
                    return "ok";
                },valor->{resultadoEnSwing.set(SwingUtilities.isEventDispatchThread() && "ok".equals(valor));
                    restaurado.set(boton[0].isEnabled() && !TrabajoUI.ocupado(ventana[0])); terminada.countDown();
                },error->terminada.countDown());
                assertFalse(boton[0].isEnabled());
            });
            assertTrue(iniciada.await(10,TimeUnit.SECONDS));
            // Este evento se atiende aun mientras la consulta espera.
            SwingUtilities.invokeAndWait(()->assertTrue(TrabajoUI.ocupado(ventana[0])));
            liberar.countDown(); assertTrue(terminada.await(10,TimeUnit.SECONDS));
            assertTrue(consultaFuera.get());assertTrue(resultadoEnSwing.get());assertTrue(restaurado.get());
        } finally {
            liberar.countDown();SwingUtilities.invokeAndWait(()->{if(ventana[0]!=null)ventana[0].dispose();});
        }
    }
}
