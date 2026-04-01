package com.enterprise.controller.admin;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Named("authBean")
@SessionScoped
@Getter
@Setter
public class AuthBean implements Serializable {

    private static final DateTimeFormatter ACCESS_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final int SESSION_TIMEOUT_MINUTES = 60;

    private String username;
    private String password;
    private String nomeUsuarioLogado;
    private String ultimoAcessoFormatado;

    public String login() {
        if (username == null || username.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuario e obrigatorio."));
            return null;
        }
        if (password == null || password.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Senha e obrigatoria."));
            return null;
        }

        nomeUsuarioLogado = formatDisplayName(username);
        ultimoAcessoFormatado = LocalDateTime.now().format(ACCESS_FORMATTER);
        return "/pages/alunos/pageDashboard.xhtml?faces-redirect=true";
    }

    public String logout() {
        username = null;
        password = null;
        nomeUsuarioLogado = null;
        ultimoAcessoFormatado = null;
        return "/login.xhtml?faces-redirect=true";
    }

    public String getMensagemBoasVindas() {
        if (nomeUsuarioLogado == null || nomeUsuarioLogado.isBlank()) {
            return "Seja Bem Vindo!";
        }
        return "Seja Bem Vindo " + nomeUsuarioLogado + "!";
    }

    public int getTempoSessaoMinutos() {
        return SESSION_TIMEOUT_MINUTES;
    }

    private String formatDisplayName(String value) {
        String[] parts = value.trim().toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
