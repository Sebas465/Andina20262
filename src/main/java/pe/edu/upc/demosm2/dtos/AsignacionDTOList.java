package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class AsignacionDTOList {
    private String id_aula;
    private Long id_curso;
    private String modalidad;
    private Long horassemanales;

    public String getId_aula() {
        return id_aula;
    }

    public void setId_aula(String id_aula) {
        this.id_aula = id_aula;
    }

    public Long getId_curso() {
        return id_curso;
    }

    public void setId_curso(Long id_curso) {
        this.id_curso = id_curso;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public Long getHorassemanales() {
        return horassemanales;
    }

    public void setHorassemanales(Long horassemanales) {
        this.horassemanales = horassemanales;
    }
}
