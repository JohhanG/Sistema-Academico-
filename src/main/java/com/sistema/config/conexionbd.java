package com.sistema.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Delegado de compatibilidad para código que importe 'conexionbd' con minúsculas.
 * Conecta a Supabase a través de ConexionBD.
 */
public class conexionbd {

    private conexionbd() {
    }

    public static Connection getConexion() throws SQLException {
        Connection conn = ConexionBD.getConexion();
        if (conn == null || conn.isClosed()) {
            throw new SQLException("No se pudo obtener la conexión activa a Supabase.");
        }
        return conn;
    }
}
