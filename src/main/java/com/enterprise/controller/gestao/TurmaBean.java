package com.enterprise.controller.gestao;

import com.enterprise.controller.common.DetalheModalBean;
import com.enterprise.dto.gestao.TurmaDTO;
import com.enterprise.model.entity.gestao.TurmaEntity;
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

@Named("turmaBean")
@ViewScoped
@Getter
@Setter
public class TurmaBean implements Serializable {

    @Inject
    private TurmaService service;
    @Inject
    private DetalheModalBean detalheModalBean;

    private TurmaEntity filtroTurma;
    private List<TurmaEntity> turmas = new ArrayList<>();
    private TurmaEntity detalheSelecionado;
    private TurmaDTO turmaDTO = new TurmaDTO();
    private TurmaDTO detalheEdicao = new TurmaDTO();
    private boolean editandoDetalhe;

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        turmas = service.findAll();
    }

    public void filtrar() {
        try {
            List<TurmaEntity> lista = service.findAll();
            if (filtroTurma != null && filtroTurma.getId() != null) {
                turmas = lista.stream()
                        .filter(t -> t.getId().equals(filtroTurma.getId()))
                        .collect(Collectors.toList());
            } else {
                turmas = lista;
            }
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + turmas.size() + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void salvar() {
        try {
            if (turmaDTO.getId() == null) {
                service.criar(turmaDTO);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Turma criada.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void limparFormulario() {
        turmaDTO = new TurmaDTO();
    }

    public void detalhar(TurmaEntity turma) {
        detalheSelecionado = turma;
        detalheEdicao = turma == null ? new TurmaDTO() : new TurmaDTO(turma);
        editandoDetalhe = false;
        if (turma != null) {
            detalheModalBean.abrir("Detalhes da turma", "/components/modal/details/turmaDetalhes.xhtml");
        }
    }

    public List<TurmaEntity> completeTurma(String query) {
        String formatTurma = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(turma -> turma.getTurma() != null
                        && turma.getTurma().toLowerCase().startsWith(formatTurma))
                .collect(Collectors.toList());
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public void habilitarEdicaoDetalhe() {
        if (detalheSelecionado == null) {
            return;
        }
        detalheEdicao = new TurmaDTO(detalheSelecionado);
        editandoDetalhe = true;
    }

    public void cancelarEdicaoDetalhe() {
        detalheEdicao = detalheSelecionado == null ? new TurmaDTO() : new TurmaDTO(detalheSelecionado);
        editandoDetalhe = false;
    }

    public void salvarDetalhe() {
        try {
            TurmaDTO atualizado = service.atualizar(detalheEdicao);
            detalheSelecionado = service.findById(atualizado.getId()).orElse(null);
            detalheEdicao = detalheSelecionado == null ? new TurmaDTO() : new TurmaDTO(detalheSelecionado);
            editandoDetalhe = false;
            recarregarLista();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Turma atualizada.");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }
}
