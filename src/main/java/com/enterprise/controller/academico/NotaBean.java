package com.enterprise.controller.academico;

import com.enterprise.dto.academico.NotaDTO;
import com.enterprise.dto.academico.NotaLancamentoDTO;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.academico.NotaService;
import com.enterprise.service.gestao.DisciplinaService;
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

@Named("notaBean")
@ViewScoped
@Getter
@Setter
public class NotaBean implements Serializable {

    @Inject
    private NotaService notaService;
    @Inject
    private TurmaService turmaService;
    @Inject
    private DisciplinaService disciplinaService;

    private TurmaEntity filtroTurma;
    private DisciplinaEntity filtroDisciplina;
    private List<NotaLancamentoDTO> lancamentos = new ArrayList<>();
    private NotaLancamentoDTO backupEdicao;

    public void filtrar() {
        try {
            validarFiltros();
            lancamentos = notaService.listarPorTurmaEDisciplina(filtroTurma.getId(), filtroDisciplina.getId());
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Notas carregadas. Registros: " + lancamentos.size() + ".");
        } catch (Exception e) {
            lancamentos = new ArrayList<>();
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void editar(NotaLancamentoDTO registro) {
        backupEdicao = copiar(registro);
        registro.setEditando(true);
    }

    public void cancelar(NotaLancamentoDTO registro) {
        if (backupEdicao != null && registro.getBoletimId().equals(backupEdicao.getBoletimId())) {
            registro.setNotaId(backupEdicao.getNotaId());
            registro.setNota1(backupEdicao.getNota1());
            registro.setNota2(backupEdicao.getNota2());
            registro.setNota3(backupEdicao.getNota3());
            registro.setNota4(backupEdicao.getNota4());
        }
        registro.setEditando(false);
    }

    public void salvar(NotaLancamentoDTO registro) {
        try {
            NotaDTO persisted = notaService.salvar(registro);
            registro.setNotaId(persisted.getId());
            registro.setNota1(persisted.getNota1());
            registro.setNota2(persisted.getNota2());
            registro.setNota3(persisted.getNota3());
            registro.setNota4(persisted.getNota4());
            registro.setEditando(false);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Notas atualizadas para " + registro.getAluno().getNome() + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public List<TurmaEntity> completeTurma(String query) {
        String termo = query == null ? "" : query.toLowerCase();
        return turmaService.findAll().stream()
                .filter(turma -> turma.getTurma() != null && turma.getTurma().toLowerCase().contains(termo))
                .collect(Collectors.toList());
    }

    public List<DisciplinaEntity> completeDisciplina(String query) {
        String termo = query == null ? "" : query.toLowerCase();
        return disciplinaService.findAll().stream()
                .filter(disciplina -> disciplina.getDescricao() != null && disciplina.getDescricao().toLowerCase().contains(termo))
                .collect(Collectors.toList());
    }

    public List<String> getConceitos() {
        return notaService.listarConceitos();
    }

    private NotaLancamentoDTO copiar(NotaLancamentoDTO origem) {
        NotaLancamentoDTO copia = new NotaLancamentoDTO();
        copia.setBoletimId(origem.getBoletimId());
        copia.setNotaId(origem.getNotaId());
        copia.setNota1(origem.getNota1());
        copia.setNota2(origem.getNota2());
        copia.setNota3(origem.getNota3());
        copia.setNota4(origem.getNota4());
        return copia;
    }

    private void validarFiltros() {
        if (filtroTurma == null || filtroTurma.getId() == null) {
            throw new IllegalArgumentException("Selecione uma turma.");
        }
        if (filtroDisciplina == null || filtroDisciplina.getId() == null) {
            throw new IllegalArgumentException("Selecione uma disciplina.");
        }
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}
