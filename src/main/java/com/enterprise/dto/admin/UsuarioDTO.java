package com.enterprise.dto.admin;

import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.dto.gestao.ProfissionalDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private ProfissionalDTO profissional;
    private String login;
    private String senha;
    private Integer sessionTimeout;

    public UsuarioDTO(UsuarioEntity usuario){
        this.id = usuario.getId();
        if (usuario.getProfissional() != null) {
            this.profissional = new ProfissionalDTO(usuario.getProfissional());
        }
        this.login = usuario.getLogin();
        this.senha = usuario.getSenha();
        this.sessionTimeout = usuario.getSessionTimeout();
    }
}
