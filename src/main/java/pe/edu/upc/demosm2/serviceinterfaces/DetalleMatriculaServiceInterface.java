package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.entities.DetalleMatricula;

import java.util.List;
import java.util.Optional;

public interface DetalleMatriculaServiceInterface {
    public List<DetalleMatricula> list();
    public void insert(DetalleMatricula detalleMatricula);
    public Optional<DetalleMatricula> listId(Long id);
    public void update(DetalleMatricula detalleMatricula);
    public void delete(Long id);

    public List<Object[]> retiroPorCurso();
    public List<Object[]> aulasNecesariasPorGrado(Long idPeriodo);
}
