package com.enterprise.controller.academico;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.admin.SituacaoService;
import com.enterprise.service.gestao.AlunoService;
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
/**
 * Bean JSF que concentra as acoes da tela de LancamentoBean e conversa com a camada de servico.
 */

@Named("lancamentoBean")
@ViewScoped
@Getter
@Setter
public class LancamentoBean implements Serializable {

    @Inject
    private TurmaService turmaService;
    @Inject
    private AlunoTurmaService alunoTurmaService;
    @Inject
    private AlunoService alunoService;
    @Inject
    private SituacaoService situacaoService;
    @Inject
    private DetalheModalBean detalheModalBean;

    private Long turmaId;
    private Integer bimestre;
    private TurmaEntity filtroTurma;
    private AlunoTurmaDTO detalheSelecionado;
    private AlunoTurmaDTO detalheEdicao = new AlunoTurmaDTO();
    private AlunoEntity detalheAlunoSelecionado;
    private TurmaEntity detalheTurmaSelecionada;
    private SituacaoEntity detalheSituacaoSelecionada;
    private boolean editandoDetalhe;
    private List<AlunoTurmaDTO> lancamentos = new ArrayList<>();
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

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
    /**
     * Salva os dados atuais do fluxo e atualiza os elementos que dependem desse resultado.
     */

    public void salvar() {
        addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Lancamentos salvos.");
    }
    /**
     * Prepara os dados do registro selecionado e abre o componente de detalhes correspondente.
     */

    public void detalhar(AlunoTurmaDTO dto) {
        if (dto == null) {
            detalheSelecionado = null;
            detalheEdicao = new AlunoTurmaDTO();
            detalheAlunoSelecionado = null;
            detalheTurmaSelecionada = null;
            detalheSituacaoSelecionada = null;
            editandoDetalhe = false;
            return;
        }
        detalheSelecionado = copiar(dto);
        detalheEdicao = copiar(dto);
        sincronizarSeletoresDetalhe();
        editandoDetalhe = false;
        detalheModalBean.abrir("Detalhes do lancamento", "/components/modal/details/lancamentoDetalhes.xhtml");
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<TurmaEntity> completeTurma(String query) {
        String format = query == null ? "" : query.toLowerCase();
        return turmaService.findAll()
                .stream()
                .filter(turma -> turma.getTurma() != null && turma.getTurma().toLowerCase().startsWith(format))
                .collect(Collectors.toList());
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
            detalheSelecionado = alunoTurmaService.atualizar(detalheEdicao);
            detalheEdicao = copiar(detalheSelecionado);
            sincronizarSeletoresDetalhe();
            editandoDetalhe = false;
            carregar();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Lancamento atualizado.");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    private void preencherDtoDetalhe() {
        if (detalheAlunoSelecionado == null || detalheTurmaSelecionada == null || detalheSituacaoSelecionada == null) {
            throw new IllegalArgumentException("Aluno, turma e situacao sao obrigatorios.");
        }
        detalheEdicao.setAluno(new AlunoDTO(detalheAlunoSelecionado));
        detalheEdicao.setTurma(new com.enterprise.dto.gestao.TurmaDTO(detalheTurmaSelecionada));
        detalheEdicao.setSituacao(new SituacaoDTO(detalheSituacaoSelecionada));
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    private void sincronizarSeletoresDetalhe() {
        detalheAlunoSelecionado = detalheEdicao != null && detalheEdicao.getAluno() != null && detalheEdicao.getAluno().getId() != null
                ? alunoService.findById(detalheEdicao.getAluno().getId()).orElse(null)
                : null;
        detalheTurmaSelecionada = detalheEdicao != null && detalheEdicao.getTurma() != null && detalheEdicao.getTurma().getId() != null
                ? turmaService.findById(detalheEdicao.getTurma().getId()).orElse(null)
                : null;
        detalheSituacaoSelecionada = detalheEdicao != null && detalheEdicao.getSituacao() != null && detalheEdicao.getSituacao().getId() != null
                ? situacaoService.findById(detalheEdicao.getSituacao().getId()).orElse(null)
                : null;
    }
    /**
     * Cria uma copia simples do objeto para evitar alteracoes involuntarias na referencia original.
     */

    private AlunoTurmaDTO copiar(AlunoTurmaDTO origem) {
        AlunoTurmaDTO copia = new AlunoTurmaDTO();
        copia.setId(origem.getId());
        if (origem.getAluno() != null) {
            AlunoDTO alunoDTO = new AlunoDTO();
            alunoDTO.setId(origem.getAluno().getId());
            alunoDTO.setRm(origem.getAluno().getRm());
            alunoDTO.setNome(origem.getAluno().getNome());
            alunoDTO.setCpf(origem.getAluno().getCpf());
            copia.setAluno(alunoDTO);
        }
        if (origem.getTurma() != null) {
            com.enterprise.dto.gestao.TurmaDTO turmaDTO = new com.enterprise.dto.gestao.TurmaDTO();
            turmaDTO.setId(origem.getTurma().getId());
            turmaDTO.setCodigo(origem.getTurma().getCodigo());
            turmaDTO.setTurma(origem.getTurma().getTurma());
            copia.setTurma(turmaDTO);
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
