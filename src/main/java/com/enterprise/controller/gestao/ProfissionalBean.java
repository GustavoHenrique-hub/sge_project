package com.enterprise.controller.gestao;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.gestao.ProfissionalDTO;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import com.enterprise.service.gestao.ProfissionalService;
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

@Named("profissionalBean")
@ViewScoped
@Getter
@Setter
public class ProfissionalBean implements Serializable {

    @Inject
    private ProfissionalService service;
    @Inject
    private DetalheModalBean detalheModalBean;

    private ProfissionalDTO profissionalDTO = new ProfissionalDTO();
    private ProfissionalEntity filtroProfissional = new ProfissionalEntity();
    private ProfissionalDTO detalheProfissional;
    private ProfissionalDTO detalheEdicao = new ProfissionalDTO();
    private boolean editandoDetalhe;
    private List<ProfissionalEntity> profissionais = new ArrayList<>();

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    public void salvar() {
        try {
            if (profissionalDTO.getId() == null) {
                service.criar(profissionalDTO);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Profissional criado.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private void recarregarLista() {
        profissionais = service.findAll();
    }

    public void limparFormulario() {
        profissionalDTO = new ProfissionalDTO();
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public List<ProfissionalEntity> completeNomeProfissional(String query) {
        String formatProfissional = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(profissional -> profissional.getNome() != null
                        && profissional.getNome().toLowerCase().startsWith(formatProfissional))
                .collect(Collectors.toList());
    }

    public List<ProfissionalEntity> completeProfissionalBuscaGeral(String query) {
        String termo = query == null ? "" : query.trim().toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");
        return service.findAll()
                .stream()
                .filter(profissional -> correspondeBuscaProfissional(profissional, termo, termoNumerico))
                .collect(Collectors.toList());
    }

    private boolean correspondeBuscaProfissional(ProfissionalEntity profissional, String termo, String termoNumerico) {
        if (profissional == null) {
            return false;
        }
        boolean buscaVazia = termo.isBlank() && termoNumerico.isBlank();
        if (buscaVazia) {
            return true;
        }

        boolean nomeCorresponde = profissional.getNome() != null
                && profissional.getNome().toLowerCase().contains(termo);
        boolean rmCorresponde = profissional.getRm() != null
                && profissional.getRm().toLowerCase().contains(termo);
        boolean cpfCorresponde = !termoNumerico.isBlank()
                && profissional.getCpf() != null
                && profissional.getCpf().contains(termoNumerico);

        return nomeCorresponde || rmCorresponde || cpfCorresponde;
    }

    public void filtrar() {
        try {
            String nome = filtroProfissional != null ? filtroProfissional.getNome() : null;
            String cpf = filtroProfissional != null ? filtroProfissional.getCpf() : null;
            boolean filtroVazio = (nome == null || nome.isBlank()) && (cpf == null || cpf.isBlank());

            if (filtroVazio) {
                profissionais = service.findAll();
            } else {
                profissionais = service.findByFilters(nome, cpf);
            }
            int total = profissionais == null ? 0 : profissionais.size();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + total + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void detalhar(ProfissionalEntity entity) {
        if (entity == null) {
            detalheProfissional = null;
            detalheEdicao = new ProfissionalDTO();
            editandoDetalhe = false;
            return;
        }
        detalheProfissional = new ProfissionalDTO(entity);
        detalheEdicao = copiar(detalheProfissional);
        editandoDetalhe = false;
        detalheModalBean.abrir("Detalhes do profissional", "/components/modal/details/profissionalDetalhes.xhtml");
    }

    public void habilitarEdicaoDetalhe() {
        if (detalheProfissional == null) {
            return;
        }
        detalheEdicao = copiar(detalheProfissional);
        editandoDetalhe = true;
    }

    public void cancelarEdicaoDetalhe() {
        detalheEdicao = copiar(detalheProfissional);
        editandoDetalhe = false;
    }

    public void salvarDetalhe() {
        try {
            detalheProfissional = service.atualizar(detalheEdicao);
            detalheEdicao = copiar(detalheProfissional);
            editandoDetalhe = false;
            recarregarLista();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Profissional atualizado.");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    private ProfissionalDTO copiar(ProfissionalDTO origem) {
        ProfissionalDTO copia = new ProfissionalDTO();
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


