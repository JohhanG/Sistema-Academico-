package com.sistema.servicio;

import com.sistema.modelo.Grupo;
import com.sistema.modelo.GrupoDAO;

import java.sql.SQLException;
import java.util.List;

// Servicio con las validaciones de negocio para Grupo
public class GrupoService {

    private final GrupoDAO grupoDAO;

    public GrupoService() {
        this.grupoDAO = new GrupoDAO();
    }

    public GrupoService(GrupoDAO grupoDAO) {
        if (grupoDAO == null) {
            throw new IllegalArgumentException("El DAO de grupo no puede ser nulo.");
        }
        this.grupoDAO = grupoDAO;
    }

    public int crearGrupo(int idMateria, int idDocente, String aula, String horario) throws SQLException {
        Grupo grupo = new Grupo(idMateria, idDocente,
                aula != null ? aula.trim() : null,
                horario != null ? horario.trim() : null);
        validarGrupo(grupo);
        return grupoDAO.insertar(grupo);
    }

    public List<Grupo> listarGrupos() throws SQLException {
        return grupoDAO.listarTodos();
    }

    public Grupo buscarGrupoPorId(int idGrupo) throws SQLException {
        if (idGrupo <= 0) {
            throw new IllegalArgumentException("El ID del grupo debe ser mayor a cero.");
        }
        return grupoDAO.buscarPorId(idGrupo);
    }

    public boolean actualizarGrupo(Grupo grupo) throws SQLException {
        if (grupo == null || grupo.getIdGrupo() <= 0) {
            throw new IllegalArgumentException("El ID del grupo no es valido.");
        }
        validarGrupo(grupo);
        grupo.setAula(grupo.getAula().trim());
        grupo.setHorario(grupo.getHorario().trim());
        return grupoDAO.actualizar(grupo);
    }

    public boolean eliminarGrupo(int idGrupo) throws SQLException {
        if (idGrupo <= 0) {
            throw new IllegalArgumentException("El ID del grupo debe ser mayor a cero.");
        }
        return grupoDAO.eliminar(idGrupo);
    }

    public List<String> obtenerComboMaterias() {
        return grupoDAO.obtenerComboMaterias();
    }

    public List<String> obtenerComboDocentes() {
        return grupoDAO.obtenerComboDocentes();
    }

    private void validarGrupo(Grupo grupo) {
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo no puede ser nulo.");
        }
        if (grupo.getIdMateria() <= 0) {
            throw new IllegalArgumentException("El ID de la materia debe ser mayor a cero.");
        }
        if (grupo.getIdDocente() <= 0) {
            throw new IllegalArgumentException("El ID del docente debe ser mayor a cero.");
        }
        grupo.setAula(Validaciones.texto(grupo.getAula(),"El aula",50));
        grupo.setHorario(Validaciones.texto(grupo.getHorario(),"El horario",100));
        if (grupo.getAula() == null || grupo.getAula().trim().isEmpty()) {
            throw new IllegalArgumentException("El aula no puede estar vacia.");
        }
        if (grupo.getHorario() == null || grupo.getHorario().trim().isEmpty()) {
            throw new IllegalArgumentException("El horario no puede estar vacio.");
        }
    }
}
