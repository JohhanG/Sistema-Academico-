package com.sistema.modelo;

/**
 * Entidad que representa un Usuario del Sistema Académico (tabla 'usuario').
 * Gestiona la autenticación y los roles de acceso ('ADMIN', 'DOCENTE', 'ESTUDIANTE').
 */
public class Usuario {

    private int idUsuario;
    private String username;
    private String password;
    private String rol;
    private Integer idEstudiante;
    private Integer idDocente;

    public Usuario() {
    }

    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Usuario(int idUsuario, String username, String password, String rol, Integer idEstudiante, Integer idDocente) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.idEstudiante = idEstudiante;
        this.idDocente = idDocente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Integer getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Integer idDocente) {
        this.idDocente = idDocente;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", rol='" + rol + '\'' +
                ", idEstudiante=" + idEstudiante +
                ", idDocente=" + idDocente +
                '}';
    }
}
