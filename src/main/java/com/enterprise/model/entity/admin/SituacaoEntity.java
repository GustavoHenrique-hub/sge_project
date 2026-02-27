package com.enterprise.model.entity.admin;

import com.enterprise.dto.admin.SituacaoDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "situacao")
public class SituacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String situacao;
    private String descricao;

    public SituacaoEntity(SituacaoDTO situacao){
        this.id = situacao.getId();
        this.situacao = situacao.getSituacao();
        this.descricao = situacao.getDescricao();
    }
}