package com.enterprise.controller.gestao;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.ProfessorDisciplinaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.ProfessorEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.dto.gestao.ProfessorDTO;
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
    @Inject
    private DetalheModalBean detalheModalBean;

    private Long professorId;
    private Long disciplinaId;
    private Long situacaoId;
    private List<ProfessorEntity> professores = new ArrayList<>();
    private List<DisciplinaEntity> disciplinas = new ArrayList<>();
    private ProfessorEntity filtroProfessor;
    private DisciplinaEntity filtroDisciplina;
    private SituacaoEntity filtroSituacao;
    private ProfessorDisciplinaDTO detalheSelecionado;
    private ProfessorDisciplinaDTO detalheEdicao = new ProfessorDisciplinaDTO();
    private ProfessorEntity detalheProfessorSelecionado;
    private DisciplinaEntity detalheDisciplinaSelecionada;
    private SituacaoEntity detalheSituacaoSelecionada;
    private boolean editandoDetalhe;
    private List<ProfessorDisciplinaDTO> professorDisciplinas = new ArrayList<>();

    @PostConstruct
    public void init() {
        professores = professorService.findAll();
        disciplinas = disciplinaService.findAll();
        recarregarLista();
    }

    private void recarregarLista() {
        professorDisciplinas = service.listarDTO();
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
        if (dto == null) {
            detalheSelecionado = null;
            detalheEdicao = new ProfessorDisciplinaDTO();
            detalheProfessorSelecionado = null;
            detalheDisciplinaSelecionada = null;
            detalheSituacaoSelecionada = null;
            editandoDetalhe = false;
            return;
        }
        detalheSelecionado = copiar(dto);
        detalheEdicao = copiar(dto);
        sincronizarSeletoresDetalhe();
        editandoDetalhe = false;
        detalheModalBean.abrir("Detalhes do vinculo", "/components/modal/details/professorDisciplinaDetalhes.xhtml");
    }

    public List<ProfessorEntity> completeProfessor(String query) {
        String formatProfessor = query == null ? "" : query.toLowerCase();
        return professorService.findAll()
                .stream()
                .filter(professor -> professor.getNome() != null
                        && professor.getNome().toLowerCase().startsWith(formatProfessor))
                .collect(Collectors.toList());
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public void habilitarEdicaoDetalhe() {
        if (detalheSelecionado == null) {
            return;
        }
        detalheEdicao = copiar(detalheSelecionado);
        sincronizarSeletoresDetalhe();
        editandoDetalhe = true;
    }

    public void cancelarEdicaoDetalhe() {
        detalheEdicao = copiar(detalheSelecionado);
        sincronizarSeletoresDetalhe();
        editandoDetalhe = false;
    }

    public void salvarDetalhe() {
        try {
            preencherDtoDetalhe();
            detalheSelecionado = service.atualizar(detalheEdicao);
            detalheEdicao = copiar(detalheSelecionado);
            sincronizarSeletoresDetalhe();
            editandoDetalhe = false;
            recarregarLista();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Vinculo atualizado.");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void preencherDtoDetalhe() {
        if (detalheProfessorSelecionado == null || detalheDisciplinaSelecionada == null || detalheSituacaoSelecionada == null) {
            throw new IllegalArgumentException("Professor, disciplina e situacao sao obrigatorios.");
        }
        detalheEdicao.setProfessor(new ProfessorDTO(detalheProfessorSelecionado));
        detalheEdicao.setDisciplina(new DisciplinaDTO(detalheDisciplinaSelecionada));
        detalheEdicao.setSituacao(new SituacaoDTO(detalheSituacaoSelecionada));
    }

    private void sincronizarSeletoresDetalhe() {
        detalheProfessorSelecionado = detalheEdicao != null && detalheEdicao.getProfessor() != null && detalheEdicao.getProfessor().getId() != null
                ? professorService.findById(detalheEdicao.getProfessor().getId()).orElse(null)
                : null;
        detalheDisciplinaSelecionada = detalheEdicao != null && detalheEdicao.getDisciplina() != null && detalheEdicao.getDisciplina().getId() != null
                ? disciplinaService.findById(detalheEdicao.getDisciplina().getId()).orElse(null)
                : null;
        detalheSituacaoSelecionada = detalheEdicao != null && detalheEdicao.getSituacao() != null && detalheEdicao.getSituacao().getId() != null
                ? situacaoService.findById(detalheEdicao.getSituacao().getId()).orElse(null)
                : null;
    }

    private ProfessorDisciplinaDTO copiar(ProfessorDisciplinaDTO origem) {
        ProfessorDisciplinaDTO copia = new ProfessorDisciplinaDTO();
        copia.setId(origem.getId());
        if (origem.getProfessor() != null) {
            ProfessorDTO professorDTO = new ProfessorDTO();
            professorDTO.setId(origem.getProfessor().getId());
            professorDTO.setRm(origem.getProfessor().getRm());
            professorDTO.setNome(origem.getProfessor().getNome());
            copia.setProfessor(professorDTO);
        }
        if (origem.getDisciplina() != null) {
            DisciplinaDTO disciplinaDTO = new DisciplinaDTO();
            disciplinaDTO.setId(origem.getDisciplina().getId());
            disciplinaDTO.setCodigo(origem.getDisciplina().getCodigo());
            disciplinaDTO.setDescricao(origem.getDisciplina().getDescricao());
            copia.setDisciplina(disciplinaDTO);
        }
        if (origem.getSituacao() != null) {
            SituacaoDTO situacaoDTO = new SituacaoDTO();
            situacaoDTO.setId(origem.getSituacao().getId());
            situacaoDTO.setSituacao(origem.getSituacao().getSituacao());
            situacaoDTO.setDescricao(origem.getSituacao().getDescricao());
            copia.setSituacao(situacaoDTO);
        }
        return copia;
    }
}
