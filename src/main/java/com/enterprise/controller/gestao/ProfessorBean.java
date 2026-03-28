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

    public List<ProfessorEntity> completeCPFProfessor(String query) {
        String formatProfessor = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(professor -> professor.getCpf() != null
                        && professor.getCpf().toLowerCase().startsWith(formatProfessor))
                .collect(Collectors.toList());
    }

    public void filtrar() {
        try {
            Long professorIdFiltro = filtroProfessor == null ? null : filtroProfessor.getId();
            boolean filtroVazio = professorIdFiltro == null;

            if (filtroVazio) {
                professores = service.findAll();
            } else {
                professores = service.findByFilters(filtroProfessor.getNome(), filtroProfessor.getCpf());
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
        copia.setCpf(origem.getCpf());
        copia.setRg(origem.getRg());
        copia.setDtNasc(origem.getDtNasc());
        copia.setEmail(origem.getEmail());
        copia.setTelefone(origem.getTelefone());
        return copia;
    }

}
