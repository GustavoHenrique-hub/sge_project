package com.enterprise.controller.gestao;

import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.service.gestao.DisciplinaService;
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

@Named("disciplinaBean")
@ViewScoped
@Getter
@Setter
public class DisciplinaBean implements Serializable {

    @Inject
    private DisciplinaService service;

    private DisciplinaEntity filtroDisciplina;
    private List<DisciplinaEntity> disciplinas = new ArrayList<>();
    private DisciplinaEntity detalheSelecionado;
    private DisciplinaDTO disciplinaDTO = new DisciplinaDTO();

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        disciplinas = service.findAll();
    }

    public void filtrar() {
        try {
            List<DisciplinaEntity> lista = service.findAll();
            if (filtroDisciplina != null && filtroDisciplina.getId() != null) {
                disciplinas = lista.stream()
                        .filter(t -> t.getId().equals(filtroDisciplina.getId()))
                        .collect(Collectors.toList());
            } else {
                disciplinas = lista;
            }
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + disciplinas.size() + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void salvar() {
        try {
            if (disciplinaDTO.getId() == null) {
                service.criar(disciplinaDTO);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Disciplina criada.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void limparFormulario() {
        disciplinaDTO = new DisciplinaDTO();
    }

    public void detalhar(DisciplinaEntity disciplina) {
        detalheSelecionado = disciplina;
    }

    public List<DisciplinaEntity> completeDisciplina(String query) {
        String formatDisciplina = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(disciplina -> disciplina.getDescricao() != null
                        && disciplina.getDescricao().toLowerCase().startsWith(formatDisciplina))
                .collect(Collectors.toList());
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}