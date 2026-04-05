package com.enterprise.dto.admin;

import com.enterprise.model.entity.admin.PerfilEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO usado para transportar dados de PerfilDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class PerfilDTO {
    private Long id;
    private String perfil;
    private String descricao;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public PerfilDTO(PerfilEntity acesso){
        this.id = acesso.getId();
        this.perfil = acesso.getPerfil();
        this.descricao = acesso.getDescricao();
    }
}