package com.enterprise.controller.app;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named("lancamentoBean")
@RequestScoped
@Getter
@Setter
public class LancamentoBean {
    private Long turmaId;
    private Integer bimestre;

    public void carregar() {
        // Placeholder para evitar erro de EL enquanto a feature nao e implementada.
    }

    public void salvar() {
        // Placeholder para evitar erro de EL enquanto a feature nao e implementada.
    }
}
