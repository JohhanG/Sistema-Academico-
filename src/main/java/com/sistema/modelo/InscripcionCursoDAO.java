package com.sistema.modelo;

import com.sistema.config.ConexionBD;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// DAO para gestionar matriculas y notas en la tabla inscripcion_curso
public class InscripcionCursoDAO {

    public boolean matricular(InscripcionCurso inscripcion) {
        String sql = "INSERT INTO inscripcion_curso (id_estudiante, id_grupo, nota_final, estado) VALUES (?, ?, ?, ?)";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, inscripcion.getIdEstudiante());
            ps.setInt(2, inscripcion.getIdGrupo());

            if (inscripcion.getNotaFinal() != null) {
                ps.setDouble(3, inscripcion.getNotaFinal());
            } else {
                ps.setNull(3, Types.NUMERIC);
            }

            ps.setString(4, inscripcion.getEstado() != null ? inscripcion.getEstado() : "Matriculado");

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        inscripcion.setIdInscripcion(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
        return false;
    }

    public List<InscripcionCurso> listar() {
        List<InscripcionCurso> lista = new ArrayList<>();
        String sql = "SELECT i.id_inscripcion, i.id_estudiante, i.id_grupo, i.nota_final, i.estado, " +
                     "e.nombre || ' ' || e.apellido AS nombre_estudiante, " +
                     "m.nombre_materia, m.creditos, d.nombre AS nombre_docente, " +
                     "g.aula || ' - ' || g.horario AS aula_horario, " +
                     "m.nombre_materia || ' (Aula: ' || g.aula || ')' AS info_grupo " +
                     "FROM inscripcion_curso i " +
                     "JOIN estudiante e ON i.id_estudiante = e.id_estudiante " +
                     "JOIN grupo g ON i.id_grupo = g.id_grupo " +
                     "JOIN materia m ON g.id_materia = m.id_materia " +
                     "JOIN docente d ON g.id_docente = d.id_docente " +
                     "ORDER BY i.id_inscripcion ASC";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return lista;
        }

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearDetalle(rs));
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
        return lista;
    }

    // Obtiene las materias y notas de un estudiante especifico
    public List<InscripcionCurso> listarPorEstudiante(int idEstudiante) {
        return listarDetalle("i.id_estudiante", idEstudiante);
    }

    public List<InscripcionCurso> listarPorDocente(int idDocente) {
        return listarDetalle("g.id_docente", idDocente);
    }

    private List<InscripcionCurso> listarDetalle(String campo, int id) {
        List<InscripcionCurso> lista = new ArrayList<>();
        String sql = "SELECT i.id_inscripcion, i.id_estudiante, i.id_grupo, i.nota_final, i.estado, " +
                     "e.nombre || ' ' || e.apellido AS nombre_estudiante, " +
                     "m.nombre_materia, m.creditos, d.nombre AS nombre_docente, " +
                     "g.aula || ' - ' || g.horario AS aula_horario, " +
                     "m.nombre_materia || ' (Aula: ' || g.aula || ')' AS info_grupo " +
                     "FROM inscripcion_curso i " +
                     "JOIN estudiante e ON i.id_estudiante = e.id_estudiante " +
                     "JOIN grupo g ON i.id_grupo = g.id_grupo " +
                     "JOIN materia m ON g.id_materia = m.id_materia " +
                     "JOIN docente d ON g.id_docente = d.id_docente " +
                     "WHERE " + campo + " = ? " +
                     "ORDER BY m.nombre_materia ASC";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return lista;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDetalle(rs));
                }
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
        return lista;
    }

    private InscripcionCurso mapearDetalle(ResultSet rs) throws SQLException {
        InscripcionCurso item = new InscripcionCurso();
        item.setIdInscripcion(rs.getInt("id_inscripcion"));
        item.setIdEstudiante(rs.getInt("id_estudiante"));
        item.setIdGrupo(rs.getInt("id_grupo"));

        double nota = rs.getDouble("nota_final");
        if (!rs.wasNull()) {
            item.setNotaFinal(nota);
        } else {
            item.setNotaFinal(null);
        }

        item.setEstado(rs.getString("estado"));
        item.setNombreEstudiante(rs.getString("nombre_estudiante"));
        item.setInfoGrupo(rs.getString("info_grupo"));
        item.setNombreMateria(rs.getString("nombre_materia"));
        item.setCreditos(rs.getInt("creditos"));
        item.setNombreDocente(rs.getString("nombre_docente"));
        item.setAulaHorario(rs.getString("aula_horario"));
        return item;
    }

    public boolean actualizar(InscripcionCurso inscripcion) {
        return actualizarCalificacion(inscripcion, null);
    }

    public boolean actualizarPorDocente(InscripcionCurso inscripcion, int idDocente) {
        return actualizarCalificacion(inscripcion, idDocente);
    }

    private boolean actualizarCalificacion(InscripcionCurso inscripcion, Integer idDocente) {
        String sql = "UPDATE inscripcion_curso SET nota_final = ?, estado = ? WHERE id_inscripcion = ? AND estado <> 'Cancelado'"
                + (idDocente == null ? "" : " AND EXISTS (SELECT 1 FROM grupo g WHERE "
                + "g.id_grupo = inscripcion_curso.id_grupo AND g.id_docente = ?)");
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (inscripcion.getNotaFinal() != null) {
                ps.setDouble(1, inscripcion.getNotaFinal());
            } else {
                ps.setNull(1, Types.NUMERIC);
            }

            ps.setString(2, inscripcion.getEstado());
            ps.setInt(3, inscripcion.getIdInscripcion());
            if (idDocente != null) ps.setInt(4, idDocente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
    }

    public boolean eliminar(int idInscripcion) {
        String sql = "UPDATE inscripcion_curso SET estado = 'Cancelado' WHERE id_inscripcion = ? AND estado <> 'Cancelado'";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
    }

    public boolean existeMatricula(int idEstudiante, int idGrupo) {
        String sql = "SELECT 1 FROM inscripcion_curso WHERE id_estudiante = ? AND id_grupo = ?";
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw com.sistema.config.ErrorDatos.desde(e);
        }
    }

    // Procedimiento almacenado 1: obtiene estudiantes para el combobox
    public List<String> obtenerComboEstudiantes() {
        List<String> items = new ArrayList<>();
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return items;
        }

        try (PreparedStatement cs = conn.prepareStatement("SELECT id_estudiante || ' - ' || nombre || ' ' || apellido AS descripcion FROM estudiante ORDER BY id_estudiante");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getString("descripcion"));
            }
        } catch (SQLException e) { throw com.sistema.config.ErrorDatos.desde(e); }
        return items;
    }

    // Procedimiento almacenado 2: obtiene grupos para el combobox
    public List<String> obtenerComboGrupos() {
        List<String> items = new ArrayList<>();
        Connection conn = ConexionBD.getConexion();
        if (conn == null) {
            return items;
        }

        try (PreparedStatement cs = conn.prepareStatement("SELECT g.id_grupo || ' - ' || m.nombre_materia || ' | ' || g.aula AS descripcion FROM grupo g JOIN materia m ON m.id_materia=g.id_materia ORDER BY g.id_grupo");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getString("descripcion"));
            }
        } catch (SQLException e) { throw com.sistema.config.ErrorDatos.desde(e); }
        return items;
    }
}
