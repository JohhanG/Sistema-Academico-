package com.sistema.config;

import java.sql.SQLException;

/** Error de persistencia que se puede presentar al usuario sin exponer credenciales. */
public class ErrorDatos extends RuntimeException {
    public ErrorDatos(String mensaje) { super(mensaje); }
    public ErrorDatos(String mensaje, Throwable causa) { super(mensaje, causa); }
    public static ErrorDatos desde(SQLException e) {
        String estado = e.getSQLState();
        String mensaje;
        if ("23505".equals(estado)) mensaje = "Ya existe un registro con esos datos. Revisa los campos unicos.";
        else if ("23503".equals(estado)) mensaje = "El registro tiene relaciones o referencias que impiden esta operacion.";
        else if ("23514".equals(estado) || "22001".equals(estado) || "23502".equals(estado))
            mensaje = "Los datos no cumplen las reglas de la base de datos. Revisa los campos.";
        else mensaje = "No se pudo completar la operacion en la base de datos. Revisa la conexion y la configuracion.";
        return new ErrorDatos(mensaje, e);
    }
}
