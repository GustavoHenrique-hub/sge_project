package com.enterprise.controller.app;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Named("authBean")
@SessionScoped
@Getter
@Setter
public class AuthBean implements Serializable {

    private String username;
    private String password;

    public String login() {
        return "/pages/alunos/dashboard.xhtml?faces-redirect=true";
    }
}
