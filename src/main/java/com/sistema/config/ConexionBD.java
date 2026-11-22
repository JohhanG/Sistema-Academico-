package com.sistema.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Configuracion sin secretos predeterminados. Las tareas JDBC de Swing se serializan. */
public final class ConexionBD {
    private static Connection conexion;
    private ConexionBD() { }

    public static Properties cargarPropiedades() {
        Properties props = new Properties();
        Path base = Path.of(System.getProperty("sistema.config.dir", "."));
        for (String nombre : new String[]{"config.properties", "config.local.properties"}) {
            Path archivo = base.resolve(nombre);
            if (Files.exists(archivo)) {
                try (InputStream entrada = Files.newInputStream(archivo)) { props.load(entrada); }
                catch (IOException e) { throw new ErrorDatos("No se pudo leer la configuracion de la base de datos.", e); }
            }
        }
        for (String[] par : new String[][]{{"DB_URL","URL"},{"DB_USER","USER"},{"DB_PASSWORD","PASSWORD"}}) {
            String valor = System.getenv(par[0]);
            if (valor != null) props.setProperty(par[1],valor);
        }
        return props;
    }

    public static synchronized Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Properties p = cargarPropiedades();
                String url = p.getProperty("URL",p.getProperty("db.url","")).trim();
                String usuario = p.getProperty("USER",p.getProperty("db.user","")).trim();
                String password = p.getProperty("PASSWORD",p.getProperty("db.password",""));
                if (!url.startsWith("jdbc:postgresql://") || usuario.isBlank() || password.isEmpty()) {
                    throw new ErrorDatos("Configura URL, USER y PASSWORD en config.local.properties o en DB_URL, DB_USER y DB_PASSWORD.");
                }
                Properties jdbc = new Properties();
                jdbc.setProperty("user",usuario);
                jdbc.setProperty("password",password);
                jdbc.setProperty("connectTimeout","10");
                jdbc.setProperty("socketTimeout","20");
                DriverManager.setLoginTimeout(10);
                conexion = DriverManager.getConnection(url,jdbc);
            }
            return conexion;
        } catch (SQLException e) { throw ErrorDatos.desde(e); }
    }

    public static synchronized void cerrarConexion() {
        try { if (conexion != null) conexion.close(); }
        catch (SQLException e) { throw ErrorDatos.desde(e); }
        finally { conexion = null; }
    }

    public static void main(String[] args) {
        try { getConexion(); System.out.println("Prueba de conexion exitosa."); }
        catch (ErrorDatos e) { System.err.println(e.getMessage()); System.exit(1); }
        finally { cerrarConexion(); }
    }
}
