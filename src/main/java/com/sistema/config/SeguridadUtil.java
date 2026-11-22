package com.sistema.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// PBKDF2 para nuevas claves; SHA-512 y MD5 solo para leer cuentas antiguas.
public class SeguridadUtil {

    // Genera hash SHA-512 de un texto
    public static String hashSHA512(String texto) {
        return generarHash(texto, "SHA-512");
    }

    // Genera hash MD5 de un texto
    public static String hashMD5(String texto) {
        return generarHash(texto, "MD5");
    }

    private static String generarHash(String texto, String algoritmo) {
        if (texto == null) {
            throw new IllegalArgumentException("El texto no puede ser nulo.");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02x", bytes[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contrasena: " + e.getMessage());
        }
    }

    private static final int ITERACIONES = 600000;

    public static String hashPassword(String texto) {
        if (texto == null || texto.isEmpty()) throw new IllegalArgumentException("La contrasena es obligatoria.");
        byte[] sal = new byte[16];
        new java.security.SecureRandom().nextBytes(sal);
        byte[] hash = derivar(texto,sal,ITERACIONES);
        return "pbkdf2$" + ITERACIONES + "$" + java.util.Base64.getEncoder().encodeToString(sal)
                + "$" + java.util.Base64.getEncoder().encodeToString(hash);
    }

    private static byte[] derivar(String texto,byte[] sal,int iteraciones) {
        javax.crypto.spec.PBEKeySpec clave = new javax.crypto.spec.PBEKeySpec(texto.toCharArray(),sal,iteraciones,256);
        try { return javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(clave).getEncoded(); }
        catch (java.security.GeneralSecurityException e) { throw new IllegalStateException("No se pudo proteger la contrasena.",e); }
        finally { clave.clearPassword(); }
    }

    public static boolean verificarPassword(String texto,String guardado) {
        if (texto == null || guardado == null) return false;
        if (guardado.startsWith("pbkdf2$")) {
            try {
                String[] partes=guardado.split("\\$",-1);
                if (partes.length!=4) return false;
                int iteraciones=Integer.parseInt(partes[1]);
                if (iteraciones<10000 || iteraciones>1000000) return false;
                byte[] sal=java.util.Base64.getDecoder().decode(partes[2]);
                byte[] esperado=java.util.Base64.getDecoder().decode(partes[3]);
                if (sal.length!=16 || esperado.length!=32) return false;
                return MessageDigest.isEqual(esperado,derivar(texto,sal,iteraciones));
            } catch (IllegalArgumentException e) { return false; }
        }
        // Compatibilidad de lectura con cuentas existentes; nunca crear hashes nuevos asi.
        if (guardado.matches("[a-fA-F0-9]{128}")) return hashSHA512(texto).equalsIgnoreCase(guardado);
        if (guardado.matches("[a-fA-F0-9]{32}")) return hashMD5(texto).equalsIgnoreCase(guardado);
        return false;
    }

    public static boolean esHashValido(String valor) {
        return valor != null && (valor.matches("[a-fA-F0-9]{128}|[a-fA-F0-9]{32}")
                || valor.matches("pbkdf2\\$[0-9]{5,7}\\$[A-Za-z0-9+/=]+\\$[A-Za-z0-9+/=]+"));
    }
}
