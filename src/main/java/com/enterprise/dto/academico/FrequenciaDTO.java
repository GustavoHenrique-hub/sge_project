package com.enterprise.dto.academico;

import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.model.entity.academico.FrequenciaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FrequenciaDTO {

    private Long id;
    private BoletimDTO boletim;
    private DisciplinaDTO disciplina;
    private BigDecimal frequencia1;
    private BigDecimal frequencia2;
    private BigDecimal frequencia3;
    private BigDecimal frequencia4;

    public FrequenciaDTO(FrequenciaEntity entity) {
        this.id = entity.getId();
        if (entity.getBoletim() != null) {
            this.boletim = new BoletimDTO();
            this.boletim.setId(entity.getBoletim().getId());
        }
        if (entity.getDisciplina() != null) {
            this.disciplina = new DisciplinaDTO(entity.getDisciplina());
        }
        this.frequencia1 = entity.getFrequencia1();
        this.frequencia2 = entity.getFrequencia2();
        this.frequencia3 = entity.getFrequencia3();
        this.frequencia4 = entity.getFrequencia4();
    }
}
