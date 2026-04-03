package com.enterprise.controller.admin;

import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.service.admin.PerfilUsuarioService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Named("authBean")
@SessionScoped
@Getter
@Setter
public class AuthBean implements Serializable {

    private static final DateTimeFormatter ACCESS_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Inject
    private PerfilUsuarioService perfilUsuarioService;

    private String username;
    private String password;
    private PerfilUsuarioEntity acessoAtual;
    private String ultimoAcessoFormatado;

    public String login() {
        if (username == null || username.isBlank()) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Usuario e obrigatorio.");
            return null;
        }
        if (password == null || password.isBlank()) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Senha e obrigatoria.");
            return null;
        }

        acessoAtual = perfilUsuarioService.autenticar(username, password).orElse(null);
        if (acessoAtual == null) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Login ou senha invalidos.");
            return null;
        }

        UsuarioEntity usuario = acessoAtual.getUsuario();
        int timeoutMinutos = usuario != null && usuario.getSessionTimeout() != null
                ? usuario.getSessionTimeout()
                : 30;

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) externalContext.getSession(true);
        session.setMaxInactiveInterval(timeoutMinutos * 60);

        ultimoAcessoFormatado = LocalDateTime.now().format(ACCESS_FORMATTER);
        password = null;
        username = usuario == null ? null : usuario.getLogin();
        return "/pages/alunos/pageDashboard.xhtml?faces-redirect=true";
    }

    public String logout() {
        invalidateSession();
        limparEstado();
        return "/login.xhtml?faces-redirect=true";
    }

    public void redirectToLogin() throws IOException {
        invalidateSession();
        limparEstado();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
    }

    public boolean isAutenticado() {
        return acessoAtual != null
                && acessoAtual.getUsuario() != null
                && acessoAtual.getPerfil() != null;
    }

    public String getNomeUsuarioLogado() {
        if (!isAutenticado()) {
            return "Visitante";
        }
        String nome = acessoAtual.getUsuario().getNomeProfissional();
        return nome == null || nome.isBlank() ? "Visitante" : nome;
    }

    public String getPerfilUsuarioLogado() {
        if (!isAutenticado() || acessoAtual.getPerfil() == null) {
            return "Sem perfil";
        }
        return acessoAtual.getPerfil().getPerfil();
    }

    public String getMensagemBoasVindas() {
        if (!isAutenticado()) {
            return "Seja Bem Vindo!";
        }
        return "Seja Bem Vindo " + getNomeUsuarioLogado() + "!";
    }

    public int getTempoSessaoMinutos() {
        if (!isAutenticado() || acessoAtual.getUsuario() == null || acessoAtual.getUsuario().getSessionTimeout() == null) {
            return 0;
        }
        return acessoAtual.getUsuario().getSessionTimeout();
    }

    public int getTempoSessaoRestanteSegundos() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return 0;
        }
        ExternalContext externalContext = context.getExternalContext();
        Object sessionObject = externalContext.getSession(false);
        if (!(sessionObject instanceof HttpSession session)) {
            return 0;
        }

        int maxInactiveInterval = session.getMaxInactiveInterval();
        if (maxInactiveInterval <= 0) {
            return 0;
        }

        long now = System.currentTimeMillis();
        long elapsedSeconds = Math.max(0L, (now - session.getLastAccessedTime()) / 1000L);
        long remainingSeconds = Math.max(0L, maxInactiveInterval - elapsedSeconds);
        return (int) remainingSeconds;
    }

    public String getTempoSessaoRestanteFormatado() {
        int totalSeconds = getTempoSessaoRestanteSegundos();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void addMensagem(FacesMessage.Severity severity, String titulo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, titulo, detalhe));
    }

    private void invalidateSession() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            return;
        }
        ExternalContext externalContext = context.getExternalContext();
        Object sessionObject = externalContext.getSession(false);
        if (sessionObject instanceof HttpSession session) {
            session.invalidate();
        }
    }

    private void limparEstado() {
        username = null;
        password = null;
        acessoAtual = null;
        ultimoAcessoFormatado = null;
    }
}
