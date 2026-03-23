package com.enterprise.controller.gestao;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.admin.SituacaoService;
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
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("alunoTurmaBean")
@ViewScoped
@Getter
@Setter
public class AlunoTurmaBean implements Serializable {

    @Inject
    private AlunoTurmaService service;
    @Inject
    private AlunoService alunoService;
    @Inject
    private TurmaService turmaService;
    @Inject
    private SituacaoService situacaoService;

    private Long alunoId;
    private Long turmaId;
    private Long situacaoId;
    private List<AlunoEntity> alunos = new ArrayList<>();
    private List<TurmaEntity> turmas = new ArrayList<>();
    private AlunoEntity filtroAluno;
    private TurmaEntity filtroTurma;
    private SituacaoEntity filtroSituacao;
    private AlunoTurmaDTO detalheSelecionado;
    private List<AlunoTurmaDTO> alunoTurmas = new ArrayList<>();

    @PostConstruct
    public void init() {
        alunos = alunoService.findAll();
        turmas = turmaService.findAll();
        recarregarLista();
    }

    private void recarregarLista() {
        alunoTurmas = service.listarDTO();
        PrimeFaces current = PrimeFaces.current();
        if (current != null) {
            current.ajax().update("formListaAluno:listaAlunos", "growl");
        }
    }

    public void limparFormulario() {
        alunoId = null;
        turmaId = null;
        filtroAluno = null;
        filtroTurma = null;
        filtroSituacao = null;
        detalheSelecionado = null;
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

    public void detalhar(AlunoTurmaDTO dto) {
        detalheSelecionado = dto;
    }

    public void confirmar() {
        try {
            service.matricular(alunoId, turmaId);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Matricula realizada.");
            alunoId = null;
            turmaId = null;
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void confirmarPelosFiltros() {
        alunoId = filtroAluno == null ? null : filtroAluno.getId();
        turmaId = filtroTurma == null ? null : filtroTurma.getId();
        confirmar();
    }

    public void filtrar() {
        try {
            Long alunoIdFiltro = filtroAluno == null ? null : filtroAluno.getId();
            Long turmaIdFiltro = filtroTurma == null ? null : filtroTurma.getId();
            Long situacaoIdFiltro = filtroSituacao == null ? null : filtroSituacao.getId();
            boolean filtroVazio = alunoIdFiltro == null && turmaIdFiltro == null && situacaoIdFiltro == null;

            if (filtroVazio) {
                alunoTurmas = service.listarDTO();
            } else {
                alunoTurmas = service.listarPorFiltros(alunoIdFiltro, turmaIdFiltro, situacaoIdFiltro);
            }
            int total = alunoTurmas == null ? 0 : alunoTurmas.size();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + total + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public List<AlunoEntity> completeAluno(String query) {
        String formatAluno = query == null ? "" : query.toLowerCase();
        return alunoService.findAll()
                .stream()
                .filter(aluno -> aluno.getNome() != null
                        && aluno.getNome().toLowerCase().startsWith(formatAluno))
                .collect(Collectors.toList());
    }

    public List<TurmaEntity> completeTurma(String query) {
        String formatTurma = query == null ? "" : query.toLowerCase();
        return turmaService.findAll()
                .stream()
                .filter(turma -> turma.getTurma() != null
                        && turma.getTurma().toLowerCase().startsWith(formatTurma))
                .collect(Collectors.toList());
    }

    public List<SituacaoEntity> completeSituacao(String query) {
        String formatSituacao = query == null ? "" : query.toLowerCase();
        return situacaoService.findAll()
                .stream()
                .filter(situacao -> situacao.getSituacao() != null
                        && situacao.getSituacao().toLowerCase().startsWith(formatSituacao))
                .collect(Collectors.toList());
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}
