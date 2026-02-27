package com.enterprise.dto.gestao;

import com.enterprise.model.entity.gestao.TurmaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TurmaDTO {

    private Long id;
    private String codigo;
    private String turma;

    public TurmaDTO(TurmaEntity turma) {
        this.id = turma.getId();
        this.codigo = turma.getCodigo();
        this.turma = turma.getTurma();
    }
}
