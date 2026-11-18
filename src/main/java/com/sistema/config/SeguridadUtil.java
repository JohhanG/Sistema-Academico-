package com.sistema.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Clase utilitaria para encriptar contrasenas con SHA-512 o MD5
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

    // Compara la contrasena ingresada con la guardada en base de datos
    public static boolean verificarPassword(String passwordPlano, String hashGuardado) {
        if (passwordPlano == null || hashGuardado == null || hashGuardado.isEmpty()) {
            return false;
        }
        String limpio = hashGuardado.trim();
        if (limpio.length() == 128) {
            return hashSHA512(passwordPlano).equalsIgnoreCase(limpio);
        }
        if (limpio.length() == 32) {
            return hashMD5(passwordPlano).equalsIgnoreCase(limpio);
        }
        return passwordPlano.equals(limpio);
    }

    // Revisa si el texto ya tiene longitud de hash SHA-512 (128) o MD5 (32)
    public static boolean esHashValido(String valor) {
        if (valor == null) {
            return false;
        }
        int len = valor.trim().length();
        return len == 128 || len == 32;
    }
}
