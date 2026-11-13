package com.sistema.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor de Conexión a PostgreSQL (Supabase) utilizando el patrón Singleton.
 */
public class ConexionBD {

    // =========================================================================
    // PARÁMETROS DE CONEXIÓN A SUPABASE
    // (Encuéntralos en: Supabase Dashboard -> Project Settings -> Database)
    // =========================================================================
    
    // 1. Host del proyecto (ej: aws-0-us-east-1.pooler.supabase.com o db.xxxx.supabase.co)
    private static final String HOST = "db.qwtdiaeruuxshrmxsaoo.supabase.co";

    // 2. Puerto estándar de PostgreSQL (5432) o Session Pooler (6543)
    private static final String PUERTO = "5432";

    // 3. Nombre de la base de datos (por defecto en Supabase es "postgres")
    private static final String BD = "postgres";

    // 4. Usuario de la base de datos (por defecto "postgres")
    private static final String USUARIO = "postgres";

    // 5. Contraseña que definiste al crear el proyecto en Supabase
    private static final String PASSWORD = "heidyexpone1";

    // URL JDBC con SSL requerido por Supabase
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + BD + "?sslmode=require";

    // Instancia única de la conexión
    private static Connection conexion = null;

    // Constructor privado para evitar instanciación directa (Singleton)
    private ConexionBD() {}

    /**
     * Obtiene una conexión activa a la base de datos.
     * Si no existe o se cerró, crea una nueva.
     *
     * @return Objeto Connection activo, o null si falla
     */
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Cargar el driver JDBC de PostgreSQL
                Class.forName("org.postgresql.Driver");

                // Establecer la conexión
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("✅ [Conexión BD] Conectado exitosamente a Supabase.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ [Error] Driver de PostgreSQL no encontrado en el proyecto.");
            System.err.println("   Asegúrate de tener la dependencia en tu pom.xml: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ [Error] Falló la conexión a Supabase: " + e.getMessage());
            System.err.println("   Verifica: HOST, PUERTO, USUARIO y PASSWORD en ConexionBD.java.");
        }
        return conexion;
    }

    /**
     * Cierra la conexión activa de forma segura.
     */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("🔒 [Conexión BD] Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("❌ [Error] Error al cerrar la conexión: " + e.getMessage());
        }
    }

    /**
     * Método main para probar la conexión directamente desde la consola.
     */
    public static void main(String[] args) {
        System.out.println("🔄 Probando conexión a Supabase...");
        Connection conn = ConexionBD.getConexion();
        if (conn != null) {
            System.out.println("🎉 ¡Conexión comprobada con éxito!");
            ConexionBD.cerrarConexion();
        } else {
            System.out.println("⚠️ No se pudo conectar. Revisa las credenciales configuradas.");
        }
    }
}
