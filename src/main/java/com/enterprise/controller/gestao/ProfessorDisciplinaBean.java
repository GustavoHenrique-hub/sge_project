package com.enterprise.controller.gestao;

import com.enterprise.dto.gestao.ProfessorDisciplinaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.ProfessorEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.service.admin.SituacaoService;
import com.enterprise.service.gestao.ProfessorService;
import com.enterprise.service.gestao.ProfessorDisciplinaService;
import com.enterprise.service.gestao.DisciplinaService;
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

@Named("professorDisciplinaBean")
@ViewScoped
@Getter
@Setter
public class ProfessorDisciplinaBean implements Serializable {

    @Inject
    private ProfessorDisciplinaService service;
    @Inject
    private ProfessorService professorService;
    @Inject
    private DisciplinaService disciplinaService;
    @Inject
    private SituacaoService situacaoService;

    private Long professorId;
    private Long disciplinaId;
    private Long situacaoId;
    private List<ProfessorEntity> professores = new ArrayList<>();
    private List<DisciplinaEntity> disciplinas = new ArrayList<>();
    private ProfessorEntity filtroProfessor;
    private DisciplinaEntity filtroDisciplina;
    private SituacaoEntity filtroSituacao;
    private ProfessorDisciplinaDTO detalheSelecionado;
    private List<ProfessorDisciplinaDTO> professorDisciplinas = new ArrayList<>();

    @PostConstruct
    public void init() {
        professores = professorService.findAll();
        disciplinas = disciplinaService.findAll();
        recarregarLista();
    }

    private void recarregarLista() {
        professorDisciplinas = service.listarDTO();
        PrimeFaces current = PrimeFaces.current();
        if (current != null) {
            current.ajax().update("formListaProfessor:listaProfessores", "growl");
        }
    }

    public void limparFormulario() {
        professorId = null;
        disciplinaId = null;
        filtroProfessor = null;
        filtroDisciplina = null;
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

    public void confirmar() {
        try {
            service.vincular(professorId, disciplinaId);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Matricula realizada.");
            professorId = null;
            disciplinaId = null;
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void confirmarPelosFiltros() {
        professorId = filtroProfessor == null ? null : filtroProfessor.getId();
        disciplinaId = filtroDisciplina == null ? null : filtroDisciplina.getId();
        confirmar();
    }

    public void filtrar() {
        try {
            Long professorIdFiltro = filtroProfessor == null ? null : filtroProfessor.getId();
            Long disciplinaIdFiltro = filtroDisciplina == null ? null : filtroDisciplina.getId();
            Long situacaoIdFiltro = filtroSituacao == null ? null : filtroSituacao.getId();
            boolean filtroVazio = professorIdFiltro == null && disciplinaIdFiltro == null && situacaoIdFiltro == null;

            if (filtroVazio) {
                professorDisciplinas = service.listarDTO();
            } else {
                professorDisciplinas = service.listarPorFiltros(professorIdFiltro, disciplinaIdFiltro, situacaoIdFiltro);
            }
            int total = professorDisciplinas == null ? 0 : professorDisciplinas.size();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + total + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void detalhar(ProfessorDisciplinaDTO dto) {
        detalheSelecionado = dto;
    }

    public List<ProfessorEntity> completeProfessor(String query) {
        String formatProfessor = query == null ? "" : query.toLowerCase();
        return professorService.findAll()
                .stream()
                .filter(professor -> professor.getNome() != null
                        && professor.getNome().toLowerCase().startsWith(formatProfessor))
                .collect(Collectors.toList());
    }

    public List<DisciplinaEntity> completeDisciplina(String query) {
        String formatDisciplina = query == null ? "" : query.toLowerCase();
        return disciplinaService.findAll()
                .stream()
                .filter(disciplina -> disciplina.getDescricao() != null
                        && disciplina.getDescricao().toLowerCase().startsWith(formatDisciplina))
                .collect(Collectors.toList());
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}
