package com.enterprise.controller.gestao;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.gestao.ProfessorDTO;
import com.enterprise.model.entity.gestao.ProfessorEntity;
import com.enterprise.service.gestao.ProfessorService;
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

@Named("professorBean")
@ViewScoped
@Getter
@Setter
public class ProfessorBean implements Serializable {

    @Inject
    private ProfessorService service;
    @Inject
    private DetalheModalBean detalheModalBean;

    private ProfessorDTO professorDTO = new ProfessorDTO();
    private ProfessorEntity filtroProfessor = new ProfessorEntity();
    private ProfessorDTO detalheProfessor;
    private ProfessorDTO detalheEdicao = new ProfessorDTO();
    private boolean editandoDetalhe;
    private List<ProfessorEntity> professores = new ArrayList<>();

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    public void salvar() {
        try {
            if (professorDTO.getId() == null) {
                service.criar(professorDTO);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Professor criado.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void recarregarLista() {
        professores = service.findAll();
    }

    public void limparFormulario() {
        professorDTO = new ProfessorDTO();
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public List<ProfessorEntity> completeNomeProfessor(String query) {
        String formatProfessor = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(professor -> professor.getNome() != null
                        && professor.getNome().toLowerCase().startsWith(formatProfessor))
                .collect(Collectors.toList());
    }

    public List<ProfessorEntity> completeProfessorBuscaGeral(String query) {
        String termo = query == null ? "" : query.trim().toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");
        return service.findAll()
                .stream()
                .filter(professor -> correspondeBuscaProfessor(professor, termo, termoNumerico))
                .collect(Collectors.toList());
    }

    private boolean correspondeBuscaProfessor(ProfessorEntity professor, String termo, String termoNumerico) {
        if (professor == null) {
            return false;
        }
        boolean buscaVazia = termo.isBlank() && termoNumerico.isBlank();
        if (buscaVazia) {
            return true;
        }

        boolean nomeCorresponde = professor.getNome() != null
                && professor.getNome().toLowerCase().contains(termo);
        boolean rmCorresponde = professor.getRm() != null
                && professor.getRm().toLowerCase().contains(termo);
        boolean cpfCorresponde = !termoNumerico.isBlank()
                && professor.getCpf() != null
                && professor.getCpf().contains(termoNumerico);

        return nomeCorresponde || rmCorresponde || cpfCorresponde;
    }

    public void filtrar() {
        try {
            String nome = filtroProfessor != null ? filtroProfessor.getNome() : null;
            String cpf = filtroProfessor != null ? filtroProfessor.getCpf() : null;
            boolean filtroVazio = (nome == null || nome.isBlank()) && (cpf == null || cpf.isBlank());

            if (filtroVazio) {
                professores = service.findAll();
            } else {
                professores = service.findByFilters(nome, cpf);
            }
            int total = professores == null ? 0 : professores.size();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + total + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void detalhar(ProfessorEntity entity) {
        if (entity == null) {
            detalheProfessor = null;
            detalheEdicao = new ProfessorDTO();
            editandoDetalhe = false;
            return;
        }
        detalheProfessor = new ProfessorDTO(entity);
        detalheEdicao = copiar(detalheProfessor);
        editandoDetalhe = false;
        detalheModalBean.abrir("Detalhes do professor", "/components/modal/details/professorDetalhes.xhtml");
    }

    public void habilitarEdicaoDetalhe() {
        if (detalheProfessor == null) {
            return;
        }
        detalheEdicao = copiar(detalheProfessor);
        editandoDetalhe = true;
    }

    public void cancelarEdicaoDetalhe() {
        detalheEdicao = copiar(detalheProfessor);
        editandoDetalhe = false;
    }

    public void salvarDetalhe() {
        try {
            detalheProfessor = service.atualizar(detalheEdicao);
            detalheEdicao = copiar(detalheProfessor);
            editandoDetalhe = false;
            recarregarLista();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Professor atualizado.");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private ProfessorDTO copiar(ProfessorDTO origem) {
        ProfessorDTO copia = new ProfessorDTO();
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
