package com.enterprise.controller.academico;

import com.enterprise.dto.academico.FrequenciaDTO;
import com.enterprise.dto.academico.FrequenciaLancamentoDTO;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.service.academico.FrequenciaService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("frequenciaBean")
@ViewScoped
@Getter
@Setter
public class FrequenciaBean implements Serializable {

    @Inject
    private FrequenciaService frequenciaService;
    @Inject
    private TurmaService turmaService;
    @Inject
    private DisciplinaService disciplinaService;

    private TurmaEntity filtroTurma;
    private DisciplinaEntity filtroDisciplina;
    private List<FrequenciaLancamentoDTO> lancamentos = new ArrayList<>();
    private FrequenciaLancamentoDTO backupEdicao;

    public void filtrar() {
        try {
            validarFiltros();
            lancamentos = frequenciaService.listarPorTurmaEDisciplina(filtroTurma.getId(), filtroDisciplina.getId());
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Frequencias carregadas. Registros: " + lancamentos.size() + ".");
        } catch (Exception e) {
            lancamentos = new ArrayList<>();
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void editar(FrequenciaLancamentoDTO registro) {
        backupEdicao = copiar(registro);
        registro.setEditando(true);
    }

    public void cancelar(FrequenciaLancamentoDTO registro) {
        if (backupEdicao != null && registro.getBoletimId().equals(backupEdicao.getBoletimId())) {
            registro.setFrequenciaId(backupEdicao.getFrequenciaId());
            registro.setFrequencia1(backupEdicao.getFrequencia1());
            registro.setFrequencia2(backupEdicao.getFrequencia2());
            registro.setFrequencia3(backupEdicao.getFrequencia3());
            registro.setFrequencia4(backupEdicao.getFrequencia4());
        }
        registro.setEditando(false);
    }

    public void salvar(FrequenciaLancamentoDTO registro) {
        try {
            FrequenciaDTO persisted = frequenciaService.salvar(registro);
            registro.setFrequenciaId(persisted.getId());
            registro.setFrequencia1(persisted.getFrequencia1());
            registro.setFrequencia2(persisted.getFrequencia2());
            registro.setFrequencia3(persisted.getFrequencia3());
            registro.setFrequencia4(persisted.getFrequencia4());
            registro.setEditando(false);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Frequencias atualizadas para " + registro.getAluno().getNome() + ".");
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

    private FrequenciaLancamentoDTO copiar(FrequenciaLancamentoDTO origem) {
        FrequenciaLancamentoDTO copia = new FrequenciaLancamentoDTO();
        copia.setBoletimId(origem.getBoletimId());
        copia.setFrequenciaId(origem.getFrequenciaId());
        copia.setFrequencia1(origem.getFrequencia1());
        copia.setFrequencia2(origem.getFrequencia2());
        copia.setFrequencia3(origem.getFrequencia3());
        copia.setFrequencia4(origem.getFrequencia4());
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
