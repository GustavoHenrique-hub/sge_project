package com.enterprise.controller.gestao;

import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.gestao.AlunoService;
import com.enterprise.service.gestao.AlunoTurmaService;
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

@Named("matriculaBean")
@ViewScoped
@Getter
@Setter
public class MatriculaBean implements Serializable {

    @Inject
    private AlunoTurmaService service;
    @Inject
    private AlunoService alunoService;
    @Inject
    private TurmaService turmaService;

    private Long alunoId;
    private Long turmaId;
    private List<AlunoEntity> alunos = new ArrayList<>();
    private List<TurmaEntity> turmas = new ArrayList<>();
    private List<AlunoTurmaDTO> matriculas = new ArrayList<>();

    @PostConstruct
    public void init() {
        alunos = alunoService.findAll();
        turmas = turmaService.findAll();
        matriculas = service.listarRecentesDTO(10);
    }

    public void confirmar() {
        try {
            service.matricular(alunoId, turmaId);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Matricula realizada.");
            alunoId = null;
            turmaId = null;
            matriculas = service.listarRecentesDTO(10);
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}
