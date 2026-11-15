package com.sistema.modelo;

import com.sistema.config.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de Acceso a Datos (DAO) para la entidad InscripcionCurso.
 * Gestiona matrículas y calificaciones en la base de datos PostgreSQL (Supabase).
 */
public class InscripcionCursoDAO {

    /**
     * Matricula un estudiante en un grupo determinado.
     *
     * @param inscripcion Objeto InscripcionCurso.
     * @return true si se matriculó con éxito, false en caso contrario.
     */
    public boolean matricular(InscripcionCurso inscripcion) {
        String sql = "INSERT INTO inscripcion_curso (id_estudiante, id_grupo, nota_final, estado) VALUES (?, ?, ?, ?)";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para registrar la matrícula.");
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
            System.err.println("❌ Error al matricular estudiante: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lista todas las inscripciones con los nombres del estudiante y del grupo / materia.
     *
     * @return Lista de inscripciones detalladas.
     */
    public List<InscripcionCurso> listar() {
        List<InscripcionCurso> lista = new ArrayList<>();
        String sql = "SELECT i.id_inscripcion, i.id_estudiante, i.id_grupo, i.nota_final, i.estado, " +
                     "e.nombre || ' ' || e.apellido AS nombre_estudiante, " +
                     "m.nombre_materia || ' (Aula: ' || g.aula || ')' AS info_grupo " +
                     "FROM inscripcion_curso i " +
                     "JOIN estudiante e ON i.id_estudiante = e.id_estudiante " +
                     "JOIN grupo g ON i.id_grupo = g.id_grupo " +
                     "JOIN materia m ON g.id_materia = m.id_materia " +
                     "ORDER BY i.id_inscripcion ASC";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para listar matrículas.");
            return lista;
        }

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
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

                lista.add(item);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar inscripciones: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza la nota final y el estado de una inscripción existente.
     *
     * @param inscripcion Objeto InscripcionCurso con los datos modificados.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    public boolean actualizar(InscripcionCurso inscripcion) {
        String sql = "UPDATE inscripcion_curso SET nota_final = ?, estado = ? WHERE id_inscripcion = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para actualizar inscripción.");
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

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar inscripción: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina / Cancela una matrícula por su ID.
     *
     * @param idInscripcion ID de la inscripción a eliminar.
     * @return true si se eliminó con éxito, false en caso contrario.
     */
    public boolean eliminar(int idInscripcion) {
        String sql = "DELETE FROM inscripcion_curso WHERE id_inscripcion = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) {
            System.err.println("❌ No hay conexión disponible para eliminar inscripción.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar inscripción: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica si un estudiante ya se encuentra matriculado en un grupo determinado.
     *
     * @param idEstudiante ID del estudiante.
     * @param idGrupo ID del grupo.
     * @return true si ya está registrado, false en caso contrario.
     */
    public boolean existeMatricula(int idEstudiante, int idGrupo) {
        String sql = "SELECT 1 FROM inscripcion_curso WHERE id_estudiante = ? AND id_grupo = ?";
        Connection conn = ConexionBD.getConexion();

        if (conn == null) return false;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar matrícula existente: " + e.getMessage());
        }
        return false;
    }
}
