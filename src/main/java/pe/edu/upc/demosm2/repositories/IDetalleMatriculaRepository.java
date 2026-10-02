package pe.edu.upc.demosm2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.demosm2.entities.DetalleMatricula;

import java.util.List;

@Repository
public interface IDetalleMatriculaRepository extends JpaRepository<DetalleMatricula, Long> {

    // Decisión: en qué cursos reforzar con tutorías porque pierden más alumnos (retiro + traslado).
    // Tablas: curso + detalles_matricula.
    @Query(value = "SELECT cu.nombre_curso, cu.area, COUNT(d.id_d_matricula) AS total, " +
            " SUM(CASE WHEN UPPER(d.estado) = 'RETIRADO' THEN 1 ELSE 0 END) AS retirados, " +
            " SUM(CASE WHEN UPPER(d.estado) = 'TRASLADADO' THEN 1 ELSE 0 END) AS trasladados, " +
            " ROUND(100.0 * SUM(CASE WHEN UPPER(d.estado) IN ('RETIRADO', 'TRASLADADO') THEN 1 ELSE 0 END) " +
            "       / COUNT(d.id_d_matricula), 2) AS porcentaje_perdida " +
            " FROM curso cu INNER JOIN detalles_matricula d ON d.id_curso = cu.id_curso " +
            " GROUP BY cu.id_curso, cu.nombre_curso, cu.area " +
            " ORDER BY porcentaje_perdida DESC, total DESC", nativeQuery = true)
    List<Object[]> retiroPorCurso();

    // Decisión: cuántas secciones abrir por grado en un periodo (máximo 40 alumnos por aula).
    // Tablas: grados + detalles_matricula.
    @Query(value = "SELECT g.nombre, g.nivel, COUNT(d.id_d_matricula) AS vigentes, " +
            " CAST(CEIL(COUNT(d.id_d_matricula) / 40.0) AS BIGINT) AS aulas_necesarias " +
            " FROM grados g INNER JOIN detalles_matricula d ON d.id_grado = g.id_grado " +
            " WHERE d.id_periodo = :idPeriodo AND UPPER(d.estado) = 'VIGENTE' " +
            " GROUP BY g.id_grado, g.nombre, g.nivel " +
            " ORDER BY vigentes DESC", nativeQuery = true)
    List<Object[]> aulasNecesariasPorGrado(@Param("idPeriodo") Long idPeriodo);
}
