package com.enterprise.controller.app;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named("historicoBean")
@RequestScoped
@Getter
@Setter
public class HistoricoBean {
    private String query;

    public void buscar() {
        // Placeholder para evitar erro de EL enquanto a feature nao e implementada.
    }
}
