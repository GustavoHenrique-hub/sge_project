package com.enterprise.controller.gestao;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.service.gestao.AlunoService;
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

@Named("alunoBean")
@ViewScoped
@Getter
@Setter
public class AlunoBean implements Serializable {

    @Inject
    private AlunoService service;

    private AlunoDTO alunoDTO = new AlunoDTO();
    private List<AlunoEntity> alunos = new ArrayList<>();

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    public void salvar() {
        try {
            if (alunoDTO.getId() == null) {
                service.criar(alunoDTO);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Aluno criado.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void recarregarLista() {
        alunos = service.findAll();
    }

    public void limparFormulario() {
        alunoDTO = new AlunoDTO();
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public List<AlunoEntity> completeAluno(String query) {
        String formatAluno = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(aluno -> aluno.getNome() != null
                        && aluno.getNome().toLowerCase().startsWith(formatAluno))
                .collect(Collectors.toList());
    }

}