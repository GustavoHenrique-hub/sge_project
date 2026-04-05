package com.enterprise.controller.academico;

import com.enterprise.dto.academico.HistoricoDTO;
import com.enterprise.dto.academico.HistoricoDisciplinaDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.service.academico.HistoricoService;
import com.enterprise.service.gestao.AlunoService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Bean JSF que concentra as acoes da tela de HistoricoBean e conversa com a camada de servico.
 */

@Named("historicoBean")
@ViewScoped
@Getter
@Setter
public class HistoricoBean implements Serializable {

    @Inject
    private AlunoService alunoService;
    @Inject
    private HistoricoService historicoService;

    private AlunoEntity filtroAluno;
    private HistoricoDTO historicoSelecionado;
    private List<HistoricoDisciplinaDTO> historico = new ArrayList<>();
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void visualizar() {
        try {
            validarAluno();
            historicoSelecionado = historicoService.gerarHistorico(filtroAluno.getId());
            historico = historicoSelecionado.getDisciplinas();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Historico carregado. Registros: " + historico.size() + ".");
        } catch (Exception e) {
            historicoSelecionado = null;
            historico = new ArrayList<>();
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void exportarPdf() throws IOException {
        try {
            validarAluno();
            byte[] arquivo = historicoService.gerarPdf(filtroAluno.getId());

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseContentLength(arquivo.length);
            externalContext.setResponseHeader("Content-Disposition", "inline; filename=\"historico-" + filtroAluno.getRm() + ".pdf\"");

            try (OutputStream output = externalContext.getResponseOutputStream()) {
                output.write(arquivo);
                output.flush();
            }
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<AlunoEntity> completeAluno(String query) {
        String termo = query == null ? "" : query.trim().toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");
        return alunoService.findAll().stream()
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

    private void validarAluno() {
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
