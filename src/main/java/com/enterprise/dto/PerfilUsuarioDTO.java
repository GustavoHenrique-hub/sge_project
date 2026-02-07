package com.enterprise.dto;

import com.enterprise.model.entity.admin.PefilUsuarioEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PerfilUsuarioDTO {

    private Long id;
    private PerfilDTO acesso;
    private UsuarioDTO usuario;

    public PerfilUsuarioDTO(PefilUsuarioEntity acessoUsuario){
        if(acessoUsuario != null && acessoUsuario.getAcesso() != null){
            this.acesso = new PerfilDTO(acessoUsuario.getAcesso());
        }
        if(acessoUsuario != null && acessoUsuario.getUsuario() != null){
            this.usuario = new UsuarioDTO(acessoUsuario.getUsuario());
        }
    }
}
