package com.enterprise.controller.gestao;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.dto.gestao.TurmaDTO;
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
    @Inject
    private DetalheModalBean detalheModalBean;

    private Long alunoId;
    private Long turmaId;
    private Long situacaoId;
    private List<AlunoEntity> alunos = new ArrayList<>();
    private List<TurmaEntity> turmas = new ArrayList<>();
    private AlunoEntity filtroAluno;
    private TurmaEntity filtroTurma;
    private SituacaoEntity filtroSituacao;
    private AlunoTurmaDTO detalheSelecionado;
    private AlunoTurmaDTO detalheEdicao = new AlunoTurmaDTO();
    private AlunoEntity detalheAlunoSelecionado;
    private TurmaEntity detalheTurmaSelecionada;
    private SituacaoEntity detalheSituacaoSelecionada;
    private boolean editandoDetalhe;
    private List<AlunoTurmaDTO> alunoTurmas = new ArrayList<>();

    @PostConstruct
    public void init() {
        alunos = alunoService.findAll();
        turmas = turmaService.findAll();
        recarregarLista();
    }

    private void recarregarLista() {
        alunoTurmas = service.listarDTO();
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
        detalheModalBean.abrir("Detalhes da matricula", "/components/modal/details/matriculaDetalhes.xhtml");
    }

    public void confirmar() {
        try {
            service.matricular(alunoId, turmaId);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Matricula realizada.");
            alunoId = null;
            turmaId = null;
            recarregarLista();
            limparFormulario();
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

    public List<AlunoEntity> completeAlunoDisponivel(String query) {
        String termo = query == null ? "" : query.trim().toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");

        return alunoService.findAll().stream()
                .filter(aluno -> aluno != null && aluno.getId() != null)
                .filter(aluno -> !service.possuiMatriculaAtiva(aluno.getId()))
                .filter(aluno -> correspondeBuscaAluno(aluno, termo, termoNumerico))
                .collect(Collectors.toList());
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    private boolean correspondeBuscaAluno(AlunoEntity aluno, String termo, String termoNumerico) {
        boolean buscaVazia = termo.isBlank() && termoNumerico.isBlank();
        if (buscaVazia) {
            return true;
        }

        boolean nome = aluno.getNome() != null && aluno.getNome().toLowerCase().contains(termo);
        boolean rm = aluno.getRm() != null && aluno.getRm().toLowerCase().contains(termo);
        boolean cpf = !termoNumerico.isBlank() && aluno.getCpf() != null && aluno.getCpf().contains(termoNumerico);
        return nome || rm || cpf;
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
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Matricula atualizada.");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void preencherDtoDetalhe() {
        if (detalheAlunoSelecionado == null || detalheTurmaSelecionada == null || detalheSituacaoSelecionada == null) {
            throw new IllegalArgumentException("Aluno, turma e situacao sao obrigatorios.");
        }
        AlunoDTO alunoDTO = new AlunoDTO(detalheAlunoSelecionado);
        TurmaDTO turmaDTO = new TurmaDTO(detalheTurmaSelecionada);
        SituacaoDTO situacaoDTO = new SituacaoDTO(detalheSituacaoSelecionada);
        detalheEdicao.setAluno(alunoDTO);
        detalheEdicao.setTurma(turmaDTO);
        detalheEdicao.setSituacao(situacaoDTO);
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
            TurmaDTO turmaDTO = new TurmaDTO();
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
