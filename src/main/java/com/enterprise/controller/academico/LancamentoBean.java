package com.enterprise.controller.academico;

import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.gestao.AlunoTurmaService;
import com.enterprise.service.gestao.TurmaService;
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

@Named("lancamentoBean")
@ViewScoped
@Getter
@Setter
public class LancamentoBean implements Serializable {

    @Inject
    private TurmaService turmaService;
    @Inject
    private AlunoTurmaService alunoTurmaService;

    private Long turmaId;
    private Integer bimestre;
    private TurmaEntity filtroTurma;
    private AlunoTurmaDTO detalheSelecionado;
    private List<AlunoTurmaDTO> lancamentos = new ArrayList<>();

    public void carregar() {
        try {
            Long turmaSelecionada = filtroTurma != null && filtroTurma.getId() != null
                    ? filtroTurma.getId()
                    : turmaId;
            if (turmaSelecionada == null) {
                lancamentos = alunoTurmaService.listarDTO();
            } else {
                lancamentos = alunoTurmaService.listarPorFiltros(null, turmaSelecionada, null);
            }
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Registros carregados: " + lancamentos.size() + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao carregar. " + e.getMessage());
        }
    }

    public void salvar() {
        addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Lancamentos salvos.");
    }

    public void detalhar(AlunoTurmaDTO dto) {
        detalheSelecionado = dto;
    }

    public List<TurmaEntity> completeTurma(String query) {
        String format = query == null ? "" : query.toLowerCase();
        return turmaService.findAll()
                .stream()
                .filter(turma -> turma.getTurma() != null && turma.getTurma().toLowerCase().startsWith(format))
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