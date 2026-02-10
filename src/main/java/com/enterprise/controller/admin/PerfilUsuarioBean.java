package com.enterprise.controller.admin;

import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.service.admin.PerfilUsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("perfilUsuarioBean")
@ViewScoped
@Getter
@Setter
public class PerfilUsuarioBean implements Serializable {

    @Inject
    private PerfilUsuarioService service;

    private PerfilUsuarioDTO perfilUsuarioDTO = new PerfilUsuarioDTO();
    private List<PerfilUsuarioDTO> perfilUsuarios = new ArrayList<>();


    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        perfilUsuarios = service.listarDTO();
    }

    public String situacaoSeverity(String situacao) {
        if (situacao == null) {
            return "info";
        }
        return switch (situacao.toUpperCase()) {
            case "ATIVO" -> "success";
            case "INATIVO" -> "danger";
            default -> "warning";
        };
    }

    public String situacaoBadgeClass(String situacao) {
        if (situacao == null) {
            return "badge-pill badge-muted";
        }
        return switch (situacao.toUpperCase()) {
            case "ATIVO" -> "badge-pill badge-success";
            case "INATIVO" -> "badge-pill badge-danger";
            default -> "badge-pill badge-warning";
        };
    }
}
