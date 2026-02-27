package com.enterprise.controller.academico;

import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.service.gestao.AlunoService;
import com.enterprise.service.gestao.AlunoTurmaService;
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

@Named("historicoBean")
@ViewScoped
@Getter
@Setter
public class HistoricoBean implements Serializable {

    @Inject
    private AlunoService alunoService;
    @Inject
    private AlunoTurmaService alunoTurmaService;

    private AlunoEntity filtroAluno;
    private List<AlunoTurmaDTO> historico = new ArrayList<>();
    private AlunoTurmaDTO detalheSelecionado;

    @PostConstruct
    public void init() {
        historico = alunoTurmaService.listarDTO();
    }

    public void buscar() {
        try {
            if (filtroAluno == null || filtroAluno.getId() == null) {
                historico = alunoTurmaService.listarDTO();
            } else {
                historico = alunoTurmaService.listarPorFiltros(filtroAluno.getId(), null, null);
            }
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + historico.size() + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void detalhar(AlunoTurmaDTO dto) {
        detalheSelecionado = dto;
    }

    public List<AlunoEntity> completeAluno(String query) {
        String format = query == null ? "" : query.toLowerCase();
        return alunoService.findAll()
                .stream()
                .filter(aluno -> aluno.getNome() != null && aluno.getNome().toLowerCase().startsWith(format))
                .collect(Collectors.toList());
    }

    public String situacaoSeverity(String situacao) {
        if (situacao == null) {
            return "info";
        }
        return switch (situacao.toUpperCase()) {
            case "ATIVO" -> "badge-success";
            case "INATIVO" -> "badge-danger";
            default -> "warning";
        };
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}