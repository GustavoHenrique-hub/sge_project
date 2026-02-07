package com.enterprise.dto;

import com.enterprise.model.entity.admin.UsuarioEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String usuario;
    private String login;
    private String senha;

    public UsuarioDTO(UsuarioEntity usuario){
        this.id = usuario.getId();
        this.usuario = usuario.getUsuario();
        this.login = usuario.getLogin();
        this.senha = usuario.getSenha();
    }
}
