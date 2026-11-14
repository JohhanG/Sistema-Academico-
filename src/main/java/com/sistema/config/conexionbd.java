package com.sistema.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionbd {

    // Reemplaza con los datos de tu proyecto en Supabase
    private static final String URL = "jdbc:postgresql://TU_HOST:5432/postgres";
    private static final String USUARIO = "TU_USUARIO";
    private static final String PASSWORD = "TU_PASSWORD";

    private conexionbd() {
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}