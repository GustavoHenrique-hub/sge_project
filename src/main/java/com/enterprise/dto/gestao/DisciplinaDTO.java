package com.enterprise.dto.gestao;

import com.enterprise.model.entity.gestao.DisciplinaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DisciplinaDTO {

    private Long id;
    private String codigo;
    private String descricao;

    public DisciplinaDTO(DisciplinaEntity disciplina) {
        this.id = disciplina.getId();
        this.codigo = disciplina.getCodigo();
        this.descricao = disciplina.getDescricao();
    }
}