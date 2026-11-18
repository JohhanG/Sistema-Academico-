package com.sistema.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias para la encriptacion de contrasenas
class SeguridadUtilTest {

    @Test
    void testHashSHA512() {
        String hash1 = SeguridadUtil.hashSHA512("admin123");
        String hash2 = SeguridadUtil.hashSHA512("admin123");

        assertNotNull(hash1);
        assertEquals(128, hash1.length());
        assertEquals(hash1, hash2);
    }

    @Test
    void testHashMD5() {
        String hash1 = SeguridadUtil.hashMD5("docente123");
        String hash2 = SeguridadUtil.hashMD5("docente123");

        assertNotNull(hash1);
        assertEquals(32, hash1.length());
        assertEquals(hash1, hash2);
    }

    @Test
    void testVerificarPassword() {
        String sha512 = SeguridadUtil.hashSHA512("clave123");
        String md5 = SeguridadUtil.hashMD5("clave123");

        assertTrue(SeguridadUtil.verificarPassword("clave123", sha512));
        assertFalse(SeguridadUtil.verificarPassword("otraClave", sha512));

        assertTrue(SeguridadUtil.verificarPassword("clave123", md5));
        assertFalse(SeguridadUtil.verificarPassword("otraClave", md5));
    }
}
