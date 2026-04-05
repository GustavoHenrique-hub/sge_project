package com.enterprise.controller.academico;

import com.enterprise.dto.academico.BoletimDisciplinaDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.academico.BoletimService;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Bean JSF que concentra as acoes da tela de BoletimBean e conversa com a camada de servico.
 */

@Named("boletimBean")
@ViewScoped
@Getter
@Setter
public class BoletimBean implements Serializable {

    @Inject
    private BoletimService boletimService;
    @Inject
    private TurmaService turmaService;
    @Inject
    private AlunoService alunoService;
    @Inject
    private AlunoTurmaService alunoTurmaService;

    private TurmaEntity filtroTurma;
    private AlunoEntity filtroAluno;
    private List<BoletimDisciplinaDTO> linhas = new ArrayList<>();
    /**
     * Aplica os filtros preenchidos na tela e atualiza a lista com o resultado encontrado.
     */

    public void filtrar() {
        try {
            validarFiltros();
            linhas = boletimService.montarBoletim(filtroAluno.getId(), filtroTurma.getId());
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Boletim carregado. Disciplinas: " + linhas.size() + ".");
        } catch (Exception e) {
            linhas = new ArrayList<>();
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<TurmaEntity> completeTurma(String query) {
        String termo = query == null ? "" : query.toLowerCase();
        return turmaService.findAll().stream()
                .filter(turma -> turma.getTurma() != null && turma.getTurma().toLowerCase().contains(termo))
                .collect(Collectors.toList());
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<AlunoEntity> completeAlunoDaTurma(String query) {
        if (filtroTurma == null || filtroTurma.getId() == null) {
            return new ArrayList<>();
        }
        String termo = query == null ? "" : query.trim().toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");

        List<AlunoEntity> alunos = alunoTurmaService.listarPorFiltros(null, filtroTurma.getId(), null).stream()
                .map(AlunoTurmaDTO::getAluno)
                .map(aluno -> alunoService.findById(aluno.getId()).orElse(null))
                .filter(aluno -> aluno != null)
                .sorted(Comparator.comparing(AlunoEntity::getNome, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        return alunos.stream()
                .filter(aluno -> correspondeBusca(aluno, termo, termoNumerico))
                .collect(Collectors.toList());
    }
    /**
     * Formata o valor recebido para apresentar ou salvar os dados em um padrao consistente.
     */

    public String formatarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return cpf;
        }
        return digitos.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
    /**
     * Centraliza a validacao usada para decidir se o registro atende ao filtro informado.
     */

    private boolean correspondeBusca(AlunoEntity aluno, String termo, String termoNumerico) {
        boolean buscaVazia = termo.isBlank() && termoNumerico.isBlank();
        if (buscaVazia) {
            return true;
        }
        boolean nome = aluno.getNome() != null && aluno.getNome().toLowerCase().contains(termo);
        boolean rm = aluno.getRm() != null && aluno.getRm().toLowerCase().contains(termo);
        boolean cpf = !termoNumerico.isBlank() && aluno.getCpf() != null && aluno.getCpf().contains(termoNumerico);
        return nome || rm || cpf;
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    private void validarFiltros() {
        if (filtroTurma == null || filtroTurma.getId() == null) {
            throw new IllegalArgumentException("Selecione uma turma.");
        }
        if (filtroAluno == null || filtroAluno.getId() == null) {
            throw new IllegalArgumentException("Selecione um aluno.");
        }
    }
    /**
     * Adiciona uma mensagem de retorno para orientar o usuario sobre o resultado da acao.
     */

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}
