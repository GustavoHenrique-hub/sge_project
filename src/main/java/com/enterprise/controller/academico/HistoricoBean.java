package com.enterprise.controller.academico;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.gestao.AlunoService;
import com.enterprise.service.gestao.AlunoTurmaService;
import com.enterprise.service.gestao.TurmaService;
import com.enterprise.service.admin.SituacaoService;
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
    @Inject
    private TurmaService turmaService;
    @Inject
    private SituacaoService situacaoService;
    @Inject
    private DetalheModalBean detalheModalBean;

    private AlunoEntity filtroAluno;
    private List<AlunoTurmaDTO> historico = new ArrayList<>();
    private AlunoTurmaDTO detalheSelecionado;
    private AlunoTurmaDTO detalheEdicao = new AlunoTurmaDTO();
    private AlunoEntity detalheAlunoSelecionado;
    private TurmaEntity detalheTurmaSelecionada;
    private SituacaoEntity detalheSituacaoSelecionada;
    private boolean editandoDetalhe;

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
        detalheModalBean.abrir("Detalhes do historico", "/components/modal/details/historicoDetalhes.xhtml");
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
            detalheSelecionado = alunoTurmaService.atualizar(detalheEdicao);
            detalheEdicao = copiar(detalheSelecionado);
            sincronizarSeletoresDetalhe();
            editandoDetalhe = false;
            buscar();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Historico atualizado.");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void preencherDtoDetalhe() {
        if (detalheAlunoSelecionado == null || detalheTurmaSelecionada == null || detalheSituacaoSelecionada == null) {
            throw new IllegalArgumentException("Aluno, turma e situacao sao obrigatorios.");
        }
        detalheEdicao.setAluno(new AlunoDTO(detalheAlunoSelecionado));
        detalheEdicao.setTurma(new com.enterprise.dto.gestao.TurmaDTO(detalheTurmaSelecionada));
        detalheEdicao.setSituacao(new SituacaoDTO(detalheSituacaoSelecionada));
    }

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
