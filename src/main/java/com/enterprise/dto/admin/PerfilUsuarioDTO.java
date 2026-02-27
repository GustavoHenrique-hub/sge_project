package com.enterprise.dto.admin;

import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.model.entity.admin.SituacaoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PerfilUsuarioDTO {

    private Long id;
    private PerfilDTO perfil;
    private UsuarioDTO usuario;
    private SituacaoDTO situacao;

    public PerfilUsuarioDTO(PerfilUsuarioEntity perfilUsuario){
        this.id = perfilUsuario.getId();
        if(perfilUsuario.getPerfil() != null){
            this.perfil = new PerfilDTO(perfilUsuario.getPerfil());
        }
        if(perfilUsuario.getUsuario() != null){
            this.usuario = new UsuarioDTO(perfilUsuario.getUsuario());
        }
        if(perfilUsuario.getSituacao() != null){
            this.situacao = new SituacaoDTO(perfilUsuario.getSituacao());
        }
    }
}