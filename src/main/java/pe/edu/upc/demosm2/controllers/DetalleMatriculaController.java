package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.DetalleMatriculaServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.GradoServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.MatriculaServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.PeriodoAcademicoServiceInterface;
import pe.edu.upc.demosm2.servicesinterfaces.ICursoService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-matricula")
public class DetalleMatriculaController {
    private final DetalleMatriculaServiceInterface service;
    private final MatriculaServiceInterface matriculaService;
    private final ICursoService cursoService;
    private final PeriodoAcademicoServiceInterface periodoService;
    private final GradoServiceInterface gradoService;

    public DetalleMatriculaController(DetalleMatriculaServiceInterface service, MatriculaServiceInterface matriculaService, ICursoService cursoService, PeriodoAcademicoServiceInterface periodoService, GradoServiceInterface gradoService) {
        this.service = service;
        this.matriculaService = matriculaService;
        this.cursoService = cursoService;
        this.periodoService = periodoService;
        this.gradoService = gradoService;
    }

    @GetMapping
    public ResponseEntity<List<DetalleMatriculaDTOList>> listar() {
        List<DetalleMatriculaDTOList> lista = service.list()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleMatriculaDTOList> buscarId(@PathVariable Long id) {
        DetalleMatricula detalle = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el detalle de matrícula: " + id));
        return ResponseEntity.ok(toDTO(detalle));
    }

    @PostMapping
    public ResponseEntity<DetalleMatriculaDTOList> registrar(@Valid @RequestBody DetalleMatriculaDTOInsert dto) {
        DetalleMatricula detalle = toEntity(dto);
        detalle.setIdDetalleMatricula(null);
        service.insert(detalle);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(detalle.getIdDetalleMatricula())
                .toUri();
        return ResponseEntity.created(location).body(toDTO(detalle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleMatriculaDTOList> modificar(@PathVariable Long id, @Valid @RequestBody DetalleMatriculaDTOInsert dto) {
        service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el detalle de matrícula: " + id));
        DetalleMatricula detalle = toEntity(dto);
        detalle.setIdDetalleMatricula(id);
        service.update(detalle);
        return ResponseEntity.ok(toDTO(detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        DetalleMatricula detalle = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el detalle de matrícula: " + id));
        service.delete(detalle.getIdDetalleMatricula());
        return ResponseEntity.noContent().build();
    }

    // Reporte 1 (curso + detalles_matricula): cursos que más alumnos pierden (retirados + trasladados).
    @GetMapping("/reportes/retiro-por-curso")
    public ResponseEntity<List<RetiroPorCursoDTO>> retiroPorCurso() {
        List<RetiroPorCursoDTO> lista = service.retiroPorCurso()
                .stream()
                .map(fila -> {
                    RetiroPorCursoDTO dto = new RetiroPorCursoDTO();
                    dto.setCurso((String) fila[0]);
                    dto.setArea((String) fila[1]);
                    dto.setTotalMatriculados(aLong(fila[2]));
                    dto.setRetirados(aLong(fila[3]));
                    dto.setTrasladados(aLong(fila[4]));
                    dto.setPorcentajePerdida(aDouble(fila[5]));
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(lista);
    }

    // Reporte 2 (grados + detalles_matricula): alumnos vigentes por grado en un periodo y aulas necesarias (40 por aula).
    @GetMapping("/reportes/aulas-por-grado/{idPeriodo}")
    public ResponseEntity<List<AulasPorGradoDTO>> aulasNecesariasPorGrado(@PathVariable Long idPeriodo) {
        periodoService.listId(idPeriodo)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + idPeriodo));
        List<AulasPorGradoDTO> lista = service.aulasNecesariasPorGrado(idPeriodo)
                .stream()
                .map(fila -> {
                    AulasPorGradoDTO dto = new AulasPorGradoDTO();
                    dto.setGrado((String) fila[0]);
                    dto.setNivel((String) fila[1]);
                    dto.setVigentes(aLong(fila[2]));
                    dto.setAulasNecesarias(aLong(fila[3]));
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(lista);
    }

    private DetalleMatriculaDTOList toDTO(DetalleMatricula detalle) {
        DetalleMatriculaDTOList dto = new DetalleMatriculaDTOList();
        dto.setIdDetalleMatricula(detalle.getIdDetalleMatricula());
        dto.setFechaMatricula(detalle.getFechaMatricula());
        dto.setEstado(detalle.getEstado());
        dto.setIdMatricula(detalle.getMatricula().getIdMatricula());
        dto.setIdCurso(detalle.getCurso().getId_curso());
        dto.setIdPeriodo(detalle.getPeriodo().getIdPeriodo());
        dto.setIdGrado(detalle.getGrado().getIdGrado());
        return dto;
    }

    private DetalleMatricula toEntity(DetalleMatriculaDTOInsert dto) {
        DetalleMatricula detalle = new DetalleMatricula();
        detalle.setFechaMatricula(dto.getFechaMatricula() != null ? dto.getFechaMatricula() : LocalDate.now());
        detalle.setEstado(dto.getEstado() != null ? dto.getEstado() : "VIGENTE");
        detalle.setMatricula(matriculaService.listId(dto.getIdMatricula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la matrícula: " + dto.getIdMatricula())));
        detalle.setCurso(cursoService.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el curso: " + dto.getIdCurso())));
        detalle.setPeriodo(periodoService.listId(dto.getIdPeriodo())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + dto.getIdPeriodo())));
        detalle.setGrado(gradoService.listId(dto.getIdGrado())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el grado: " + dto.getIdGrado())));
        return detalle;
    }

    private static Long aLong(Object valor) {
        return valor == null ? null : ((Number) valor).longValue();
    }

    private static Double aDouble(Object valor) {
        return valor == null ? null : ((Number) valor).doubleValue();
    }
}
