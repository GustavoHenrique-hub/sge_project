package com.enterprise.dto.admin;

import com.enterprise.model.entity.admin.SituacaoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO usado para transportar dados de SituacaoDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class SituacaoDTO {
    private Long id;
    private String situacao;
    private String descricao;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public SituacaoDTO(SituacaoEntity situacao){
        this.id = situacao.getId();
        this.situacao = situacao.getSituacao();
        this.descricao = situacao.getDescricao();
    }
}