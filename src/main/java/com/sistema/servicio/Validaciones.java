package com.sistema.servicio;

import java.util.Locale;
import java.util.regex.Pattern;

public final class Validaciones {
    private static final Pattern CORREO = Pattern.compile("^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+$");
    private Validaciones() { }
    public static String texto(String valor,String campo,int maximo) {
        if (valor == null || valor.trim().isEmpty()) throw new IllegalArgumentException(campo + " es obligatorio.");
        String limpio=valor.trim();
        if (limpio.length()>maximo) throw new IllegalArgumentException(campo + " permite hasta " + maximo + " caracteres.");
        return limpio;
    }
    public static String correo(String valor) {
        String limpio=texto(valor,"El correo",150).toLowerCase(Locale.ROOT);
        if (!CORREO.matcher(limpio).matches()) throw new IllegalArgumentException("El formato del correo no es valido.");
        return limpio;
    }
}
