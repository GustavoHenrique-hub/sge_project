package com.enterprise.controller.gestao;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.service.gestao.AlunoService;
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

@Named("alunoBean")
@ViewScoped
@Getter
@Setter
public class AlunoBean implements Serializable {

    @Inject
    private AlunoService service;
    @Inject
    private DetalheModalBean detalheModalBean;

    private AlunoDTO alunoDTO = new AlunoDTO();
    private AlunoDTO detalheSelecionado;
    private AlunoDTO detalheEdicao = new AlunoDTO();
    private boolean editandoDetalhe;
    private AlunoEntity filtroAluno;
    private List<AlunoEntity> alunos = new ArrayList<>();

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    public void salvar() {
        try {
            if (alunoDTO.getId() == null) {
                service.criar(alunoDTO);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Aluno criado.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void recarregarLista() {
        alunos = service.findAll();
    }

    public void limparFormulario() {
        alunoDTO = new AlunoDTO();
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public List<AlunoEntity> completeNomeAluno(String query) {
        String formatNomeAluno = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(aluno -> aluno.getNome() != null
                        && aluno.getNome().toLowerCase().startsWith(formatNomeAluno))
                .collect(Collectors.toList());
    }

    public List<AlunoEntity> completeAlunoBuscaGeral(String query) {
        String termo = query == null ? "" : query.trim().toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");
        return service.findAll()
                .stream()
                .filter(aluno -> correspondeBuscaAluno(aluno, termo, termoNumerico))
                .collect(Collectors.toList());
    }

    private boolean correspondeBuscaAluno(AlunoEntity aluno, String termo, String termoNumerico) {
        if (aluno == null) {
            return false;
        }
        boolean buscaVazia = termo.isBlank() && termoNumerico.isBlank();
        if (buscaVazia) {
            return true;
        }

        boolean nomeCorresponde = aluno.getNome() != null
                && aluno.getNome().toLowerCase().contains(termo);
        boolean rmCorresponde = aluno.getRm() != null
                && aluno.getRm().toLowerCase().contains(termo);
        boolean cpfCorresponde = !termoNumerico.isBlank()
                && aluno.getCpf() != null
                && aluno.getCpf().contains(termoNumerico);

        return nomeCorresponde || rmCorresponde || cpfCorresponde;
    }


    public void detalhar(AlunoDTO dto) {
        if (dto == null) {
            detalheSelecionado = null;
            detalheEdicao = new AlunoDTO();
            editandoDetalhe = false;
            return;
        }
        detalheSelecionado = copiar(dto);
        detalheEdicao = copiar(dto);
        editandoDetalhe = false;
        detalheModalBean.abrir("Detalhes do aluno", "/components/modal/details/alunoDetalhes.xhtml");
    }

    public void detalhar(AlunoEntity entity) {
        if (entity == null) {
            detalheSelecionado = null;
            detalheEdicao = new AlunoDTO();
            editandoDetalhe = false;
            return;
        }
        detalhar(new AlunoDTO(entity));
    }

    public void habilitarEdicaoDetalhe() {
        if (detalheSelecionado == null) {
            return;
        }
        detalheEdicao = copiar(detalheSelecionado);
        editandoDetalhe = true;
    }

    public void cancelarEdicaoDetalhe() {
        detalheEdicao = copiar(detalheSelecionado);
        editandoDetalhe = false;
    }

    public void salvarDetalhe() {
        try {
            detalheSelecionado = service.atualizar(detalheEdicao);
            detalheEdicao = copiar(detalheSelecionado);
            editandoDetalhe = false;
            recarregarLista();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Aluno atualizado.");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void filtrar() {
        try {
            String nome = filtroAluno == null ? null : filtroAluno.getNome();
            String cpf = filtroAluno == null ? null : filtroAluno.getCpf();
            boolean filtroVazio = (nome == null || nome.isBlank()) && (cpf == null || cpf.isBlank());

            if (filtroVazio) {
                alunos = service.findAll();
            } else {
                alunos = service.findByFilters(nome, cpf);
            }
            int total = alunos == null ? 0 : alunos.size();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + total + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    private AlunoDTO copiar(AlunoDTO origem) {
        AlunoDTO copia = new AlunoDTO();
        copia.setId(origem.getId());
        copia.setRm(origem.getRm());
        copia.setNome(origem.getNome());
        copia.setCpf(formatarCpf(origem.getCpf()));
        copia.setRg(origem.getRg());
        copia.setDtNasc(origem.getDtNasc());
        copia.setEmail(origem.getEmail());
        copia.setTelefone(origem.getTelefone());
        return copia;
    }

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

}
