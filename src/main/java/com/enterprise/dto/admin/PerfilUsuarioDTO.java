package com.enterprise.dto.admin;

import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
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
    private String situacao;

    public PerfilUsuarioDTO(PerfilUsuarioEntity acessoUsuario){
        this.id = acessoUsuario.getId();
        if(acessoUsuario.getAcesso() != null){
            this.acesso = new PerfilDTO(acessoUsuario.getAcesso());
        }
        if(acessoUsuario.getUsuario() != null){
            this.usuario = new UsuarioDTO(acessoUsuario.getUsuario());
        }
        this.situacao = acessoUsuario.getSituacao();
    }
}
