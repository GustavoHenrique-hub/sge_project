package com.enterprise.controller.admin;

import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.security.JwtUtil;
import com.enterprise.service.admin.PerfilUsuarioService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Named("authBean")
@SessionScoped
@Getter
@Setter
public class AuthBean implements Serializable {

    @Inject
    private PerfilUsuarioService perfilUsuarioService;
    @Inject
    private JwtUtil jwtUtil;

    private String username;
    private String password;
    private String usuarioLogado;
    private String nomeUsuarioLogado;
    private String perfilLogado;

    public String login() {
        PerfilUsuarioEntity acesso = perfilUsuarioService.autenticar(username, password).orElse(null);
        if (acesso == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuario, senha ou vinculo ativo invalidos."));
            return null;
        }

        UsuarioEntity usuario = acesso.getUsuario();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        usuarioLogado = usuario == null ? null : usuario.getLogin();
        nomeUsuarioLogado = usuario == null ? null : usuario.getUsuario();
        perfilLogado = acesso.getPerfil() == null ? null : acesso.getPerfil().getPerfil();

        externalContext.getSessionMap().put("token", jwtUtil.gerarToken(usuarioLogado));
        externalContext.getSessionMap().put("usuarioLogado", usuarioLogado);
        externalContext.getSessionMap().put("nomeUsuarioLogado", nomeUsuarioLogado);
        externalContext.getSessionMap().put("perfilLogado", perfilLogado);

        username = null;
        password = null;
        return "/pages/alunos/pageDashboard.xhtml?faces-redirect=true";
    }

    public String logout() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.invalidateSession();
        username = null;
        password = null;
        usuarioLogado = null;
        nomeUsuarioLogado = null;
        perfilLogado = null;
        return "/login.xhtml?faces-redirect=true";
    }
}
