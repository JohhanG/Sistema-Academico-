package com.sistema.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Clase Singleton para conectar a PostgreSQL (Supabase) usando config.properties
public class ConexionBD {

    private static final String CONFIG_FILE = "config.properties";

    private static final String DEFAULT_HOST = "db.qwtdiaeruuxshrmxsaoo.supabase.co";
    private static final String DEFAULT_PUERTO = "5432";
    private static final String DEFAULT_BD = "postgres";
    private static final String DEFAULT_USUARIO = "postgres";
    private static final String DEFAULT_PASSWORD = "heidyexpone1";

    private static Connection conexion = null;

    private ConexionBD() {
    }

    // Lee los datos del archivo config.properties
    public static Properties cargarPropiedades() {
        Properties props = new Properties();
        File archivo = new File(CONFIG_FILE);
        if (archivo.exists()) {
            try (FileInputStream fis = new FileInputStream(archivo)) {
                props.load(fis);
            } catch (IOException e) {
                System.err.println("No se pudo leer config.properties: " + e.getMessage());
            }
        }
        return props;
    }

    // Devuelve la conexion activa a la base de datos
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("org.postgresql.Driver");

                Properties props = cargarPropiedades();
                String url = props.getProperty("URL", props.getProperty("db.url", "")).trim();
                if (url.isEmpty()) {
                    String host = props.getProperty("HOST", DEFAULT_HOST).trim();
                    String port = props.getProperty("PORT", DEFAULT_PUERTO).trim();
                    String db = props.getProperty("DB", DEFAULT_BD).trim();
                    url = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?sslmode=require";
                }

                String usuario = props.getProperty("USER", props.getProperty("db.user", DEFAULT_USUARIO)).trim();
                if (usuario.isEmpty()) {
                    usuario = DEFAULT_USUARIO;
                }

                String password = props.getProperty("PASSWORD", props.getProperty("db.password", DEFAULT_PASSWORD)).trim();
                if (password.isEmpty()) {
                    password = DEFAULT_PASSWORD;
                }

                conexion = DriverManager.getConnection(url, usuario, password);
                System.out.println("Conectado exitosamente a la base de datos.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de PostgreSQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return conexion;
    }

    // Cierra la conexion si esta abierta
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexion cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection conn = ConexionBD.getConexion();
        if (conn != null) {
            System.out.println("Prueba de conexion exitosa.");
            ConexionBD.cerrarConexion();
        } else {
            System.out.println("No se pudo conectar. Revisa config.properties.");
        }
    }
}
