
package com.sistema.controlador;

import com.sistema.modelo.Grupo;
import com.sistema.modelo.GrupoDAO;

import java.sql.SQLException;
import java.util.List;

public class GrupoControlador {

    private final GrupoDAO dao;

    public GrupoControlador() {
        this.dao = new GrupoDAO();
    }

    public int crearGrupo(int idMateria, int idDocente, String aula, String horario) throws SQLException {
        if (aula == null || aula.isBlank()) {
            throw new IllegalArgumentException("El aula no puede estar vacía");
        }
        if (horario == null || horario.isBlank()) {
            throw new IllegalArgumentException("El horario no puede estar vacío");
        }
        Grupo grupo = new Grupo(idMateria, idDocente, aula.trim(), horario.trim());
        return dao.insertar(grupo);
    }

    public List<Grupo> listarGrupos() throws SQLException {
        return dao.listarTodos();
    }

    public Grupo buscarGrupo(int idGrupo) throws SQLException {
        return dao.buscarPorId(idGrupo);
    }

    public boolean actualizarGrupo(Grupo grupo) throws SQLException {
        return dao.actualizar(grupo);
    }

    public boolean eliminarGrupo(int idGrupo) throws SQLException {
        return dao.eliminar(idGrupo);
    }
}