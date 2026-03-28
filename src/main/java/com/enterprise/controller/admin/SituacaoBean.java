package com.enterprise.controller.admin;

import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.service.admin.SituacaoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("situacaoBean")
@ViewScoped
@Getter
@Setter
public class SituacaoBean implements Serializable {

    @Inject
    private SituacaoService service;

    private SituacaoEntity form = new SituacaoEntity();
    private SituacaoEntity filtroSituacao;
    private List<SituacaoEntity> situacao = new ArrayList<>();

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    public void limparFormulario() {
        form = new SituacaoEntity();
    }

    private void recarregarLista() {
        situacao = service.findAll();
    }

    public List<SituacaoEntity> completeSituacao(String query) {
        String formatSituacao = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(situacao -> situacao.getSituacao() != null
                        && situacao.getSituacao().toLowerCase().startsWith(formatSituacao))
                .collect(Collectors.toList());
    }
}