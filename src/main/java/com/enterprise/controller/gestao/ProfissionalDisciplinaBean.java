package com.enterprise.controller.gestao;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.ProfissionalDisciplinaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.dto.gestao.ProfissionalDTO;
import com.enterprise.service.admin.SituacaoService;
import com.enterprise.service.gestao.ProfissionalService;
import com.enterprise.service.gestao.ProfissionalDisciplinaService;
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
/**
 * Bean JSF que concentra as acoes da tela de ProfissionalDisciplinaBean e conversa com a camada de servico.
 */

@Named("profissionalDisciplinaBean")
@ViewScoped
@Getter
@Setter
public class ProfissionalDisciplinaBean implements Serializable {

    @Inject
    private ProfissionalDisciplinaService service;
    @Inject
    private ProfissionalService profissionalService;
    @Inject
    private DisciplinaService disciplinaService;
    @Inject
    private SituacaoService situacaoService;
    @Inject
    private DetalheModalBean detalheModalBean;

    private Long profissionalId;
    private Long disciplinaId;
    private Long situacaoId;
    private List<ProfissionalEntity> profissionais = new ArrayList<>();
    private List<DisciplinaEntity> disciplinas = new ArrayList<>();
    private ProfissionalEntity filtroProfissional;
    private DisciplinaEntity filtroDisciplina;
    private SituacaoEntity filtroSituacao;
    private ProfissionalDisciplinaDTO detalheSelecionado;
    private ProfissionalDisciplinaDTO detalheEdicao = new ProfissionalDisciplinaDTO();
    private ProfissionalEntity detalheProfissionalSelecionado;
    private DisciplinaEntity detalheDisciplinaSelecionada;
    private SituacaoEntity detalheSituacaoSelecionada;
    private boolean editandoDetalhe;
    private List<ProfissionalDisciplinaDTO> profissionalDisciplinas = new ArrayList<>();
    /**
     * Inicializa o estado da tela ou da classe assim que a instancia fica disponivel.
     */

    @PostConstruct
    public void init() {
        profissionais = profissionalService.findAll();
        disciplinas = disciplinaService.findAll();
        recarregarLista();
    }
    /**
     * Recarrega a lista exibida na interface para refletir o estado atual dos dados.
     */

    private void recarregarLista() {
        profissionalDisciplinas = service.listarDTO();
    }
    /**
     * Limpa os campos do formulario para preparar um novo cadastro ou nova consulta.
     */

    public void limparFormulario() {
        profissionalId = null;
        disciplinaId = null;
        filtroProfissional = null;
        filtroDisciplina = null;
        filtroSituacao = null;
        detalheSelecionado = null;
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

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
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void confirmar() {
        try {
            service.vincular(profissionalId, disciplinaId);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Matricula realizada.");
            profissionalId = null;
            disciplinaId = null;
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void confirmarPelosFiltros() {
        profissionalId = filtroProfissional == null ? null : filtroProfissional.getId();
        disciplinaId = filtroDisciplina == null ? null : filtroDisciplina.getId();
        confirmar();
    }
    /**
     * Aplica os filtros preenchidos na tela e atualiza a lista com o resultado encontrado.
     */

    public void filtrar() {
        try {
            Long profissionalIdFiltro = filtroProfissional == null ? null : filtroProfissional.getId();
            Long disciplinaIdFiltro = filtroDisciplina == null ? null : filtroDisciplina.getId();
            Long situacaoIdFiltro = filtroSituacao == null ? null : filtroSituacao.getId();
            boolean filtroVazio = profissionalIdFiltro == null && disciplinaIdFiltro == null && situacaoIdFiltro == null;

            if (filtroVazio) {
                profissionalDisciplinas = service.listarDTO();
            } else {
                profissionalDisciplinas = service.listarPorFiltros(profissionalIdFiltro, disciplinaIdFiltro, situacaoIdFiltro);
            }
            int total = profissionalDisciplinas == null ? 0 : profissionalDisciplinas.size();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + total + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }
    /**
     * Prepara os dados do registro selecionado e abre o componente de detalhes correspondente.
     */

    public void detalhar(ProfissionalDisciplinaDTO dto) {
        if (dto == null) {
            detalheSelecionado = null;
            detalheEdicao = new ProfissionalDisciplinaDTO();
            detalheProfissionalSelecionado = null;
            detalheDisciplinaSelecionada = null;
            detalheSituacaoSelecionada = null;
            editandoDetalhe = false;
            return;
        }
        detalheSelecionado = copiar(dto);
        detalheEdicao = copiar(dto);
        sincronizarSeletoresDetalhe();
        editandoDetalhe = false;
        detalheModalBean.abrir("Detalhes do vinculo", "/components/modal/details/profissionalDisciplinaDetalhes.xhtml");
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<ProfissionalEntity> completeProfissional(String query) {
        String formatProfissional = query == null ? "" : query.toLowerCase();
        return profissionalService.findAll()
                .stream()
                .filter(profissional -> profissional.getNome() != null
                        && profissional.getNome().toLowerCase().startsWith(formatProfissional))
                .collect(Collectors.toList());
    }
    /**
     * Adiciona uma mensagem de retorno para orientar o usuario sobre o resultado da acao.
     */

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
    /**
     * Ativa o modo de edicao da area de detalhes preservando o valor original para cancelamento.
     */

    public void habilitarEdicaoDetalhe() {
        if (detalheSelecionado == null) {
            return;
        }
        detalheEdicao = copiar(detalheSelecionado);
        sincronizarSeletoresDetalhe();
        editandoDetalhe = true;
    }
    /**
     * Descarta a edicao em andamento e restaura os dados atualmente confirmados.
     */

    public void cancelarEdicaoDetalhe() {
        detalheEdicao = copiar(detalheSelecionado);
        sincronizarSeletoresDetalhe();
        editandoDetalhe = false;
    }
    /**
     * Persiste as alteracoes feitas na visualizacao de detalhes e sincroniza a tela com o valor salvo.
     */

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
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    private void preencherDtoDetalhe() {
        if (detalheProfissionalSelecionado == null || detalheDisciplinaSelecionada == null || detalheSituacaoSelecionada == null) {
            throw new IllegalArgumentException("Profissional, disciplina e situacao sao obrigatorios.");
        }
        detalheEdicao.setProfissional(new ProfissionalDTO(detalheProfissionalSelecionado));
        detalheEdicao.setDisciplina(new DisciplinaDTO(detalheDisciplinaSelecionada));
        detalheEdicao.setSituacao(new SituacaoDTO(detalheSituacaoSelecionada));
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    private void sincronizarSeletoresDetalhe() {
        detalheProfissionalSelecionado = detalheEdicao != null && detalheEdicao.getProfissional() != null && detalheEdicao.getProfissional().getId() != null
                ? profissionalService.findById(detalheEdicao.getProfissional().getId()).orElse(null)
                : null;
        detalheDisciplinaSelecionada = detalheEdicao != null && detalheEdicao.getDisciplina() != null && detalheEdicao.getDisciplina().getId() != null
                ? disciplinaService.findById(detalheEdicao.getDisciplina().getId()).orElse(null)
                : null;
        detalheSituacaoSelecionada = detalheEdicao != null && detalheEdicao.getSituacao() != null && detalheEdicao.getSituacao().getId() != null
                ? situacaoService.findById(detalheEdicao.getSituacao().getId()).orElse(null)
                : null;
    }
    /**
     * Cria uma copia simples do objeto para evitar alteracoes involuntarias na referencia original.
     */

    private ProfissionalDisciplinaDTO copiar(ProfissionalDisciplinaDTO origem) {
        ProfissionalDisciplinaDTO copia = new ProfissionalDisciplinaDTO();
        copia.setId(origem.getId());
        if (origem.getProfissional() != null) {
            ProfissionalDTO profissionalDTO = new ProfissionalDTO();
            profissionalDTO.setId(origem.getProfissional().getId());
            profissionalDTO.setRm(origem.getProfissional().getRm());
            profissionalDTO.setNome(origem.getProfissional().getNome());
            copia.setProfissional(profissionalDTO);
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

