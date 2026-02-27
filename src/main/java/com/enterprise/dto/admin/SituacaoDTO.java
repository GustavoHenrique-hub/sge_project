package com.enterprise.dto.admin;

import com.enterprise.model.entity.admin.SituacaoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SituacaoDTO {
    private Long id;
    private String situacao;
    private String descricao;

    public SituacaoDTO(SituacaoEntity situacao){
        this.id = situacao.getId();
        this.situacao = situacao.getSituacao();
        this.descricao = situacao.getDescricao();
    }
}