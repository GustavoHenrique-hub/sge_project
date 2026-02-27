package com.enterprise.controller.gestao;

import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.gestao.TurmaService;
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

@Named("turmaBean")
@ViewScoped
@Getter
@Setter
public class TurmaBean implements Serializable {

    @Inject
    private TurmaService service;

    private TurmaEntity filtroTurma;
    private List<TurmaEntity> turmas = new ArrayList<>();
    private TurmaEntity detalheSelecionado;

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        turmas = service.findAll();
    }

    public void filtrar() {
        try {
            List<TurmaEntity> lista = service.findAll();
            if (filtroTurma != null && filtroTurma.getId() != null) {
                turmas = lista.stream()
                        .filter(t -> t.getId().equals(filtroTurma.getId()))
                        .collect(Collectors.toList());
            } else {
                turmas = lista;
            }
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + turmas.size() + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void detalhar(TurmaEntity turma) {
        detalheSelecionado = turma;
    }

    public List<TurmaEntity> completeTurma(String query) {
        String formatTurma = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(turma -> turma.getTurma() != null
                        && turma.getTurma().toLowerCase().startsWith(formatTurma))
                .collect(Collectors.toList());
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}