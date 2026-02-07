package com.enterprise.dto;

import com.enterprise.model.entity.admin.PerfilEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PerfilDTO {
    private Long id;
    private String nomeAcesso;
    private String descricao;

    public PerfilDTO(PerfilEntity acesso){

    }
}
