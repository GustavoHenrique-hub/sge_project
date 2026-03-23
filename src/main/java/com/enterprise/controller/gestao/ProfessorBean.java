package com.enterprise.controller.gestao;

import com.enterprise.dto.gestao.ProfessorDTO;
import com.enterprise.model.entity.gestao.ProfessorEntity;
import com.enterprise.service.gestao.ProfessorService;
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

@Named("professorBean")
@ViewScoped
@Getter
@Setter
public class ProfessorBean implements Serializable {

    @Inject
    private ProfessorService service;

    private ProfessorDTO professorDTO = new ProfessorDTO();
    private List<ProfessorEntity> professores = new ArrayList<>();

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    public void salvar() {
        try {
            if (professorDTO.getId() == null) {
                service.criar(professorDTO);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Professor criado.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void recarregarLista() {
        professores = service.findAll();
    }

    public void limparFormulario() {
        professorDTO = new ProfessorDTO();
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public List<ProfessorEntity> completeProfessor(String query) {
        String formatProfessor = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(professor -> professor.getNome() != null
                        && professor.getNome().toLowerCase().startsWith(formatProfessor))
                .collect(Collectors.toList());
    }

}