package pe.edu.upc.demosm2.serviceimplements;

import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.DetalleMatricula;
import pe.edu.upc.demosm2.repositories.IDetalleMatriculaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.DetalleMatriculaServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleMatriculaServiceImplement implements DetalleMatriculaServiceInterface {
    private final IDetalleMatriculaRepository repository;

    public DetalleMatriculaServiceImplement(IDetalleMatriculaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DetalleMatricula> list() {
        return repository.findAll();
    }

    @Override
    public void insert(DetalleMatricula detalleMatricula) {
        repository.save(detalleMatricula);
    }

    @Override
    public Optional<DetalleMatricula> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(DetalleMatricula detalleMatricula) {
        repository.save(detalleMatricula);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Object[]> retiroPorCurso() {
        return repository.retiroPorCurso();
    }

    @Override
    public List<Object[]> aulasNecesariasPorGrado(Long idPeriodo) {
        return repository.aulasNecesariasPorGrado(idPeriodo);
    }
}
