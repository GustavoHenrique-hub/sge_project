package com.enterprise.service.academico;

import com.enterprise.dto.academico.BoletimDTO;
import com.enterprise.dto.academico.FrequenciaDTO;
import com.enterprise.dto.academico.FrequenciaLancamentoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.academico.BoletimEntity;
import com.enterprise.model.entity.academico.FrequenciaEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.repository.academico.FrequenciaRepository;
import com.enterprise.service.gestao.AlunoTurmaService;
import com.enterprise.service.gestao.DisciplinaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequestScoped
public class FrequenciaService {

    @Inject
    private FrequenciaRepository frequenciaRepository;
    @Inject
    private DisciplinaService disciplinaService;
    @Inject
    private AlunoTurmaService alunoTurmaService;
    @Inject
    private BoletimService boletimService;

    public List<FrequenciaLancamentoDTO> listarPorTurmaEDisciplina(Long turmaId, Long disciplinaId) {
        if (turmaId == null || disciplinaId == null) {
            throw new IllegalArgumentException("Turma e disciplina sao obrigatorias.");
        }

        DisciplinaEntity disciplina = disciplinaService.findById(disciplinaId)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));

        List<FrequenciaLancamentoDTO> linhas = new ArrayList<>();
        for (AlunoTurmaDTO matricula : alunoTurmaService.listarPorFiltros(null, turmaId, null)) {
            BoletimDTO boletim = boletimService.obterOuCriar(matricula.getAluno().getId(), turmaId);
            FrequenciaDTO frequencia = frequenciaRepository.findByBoletimAndDisciplina(boletim.getId(), disciplinaId)
                    .map(FrequenciaDTO::new)
                    .orElse(null);

            FrequenciaLancamentoDTO linha = new FrequenciaLancamentoDTO();
            linha.setBoletimId(boletim.getId());
            linha.setFrequenciaId(frequencia == null ? null : frequencia.getId());
            linha.setAluno(matricula.getAluno());
            linha.setTurma(matricula.getTurma());
            linha.setDisciplina(new com.enterprise.dto.gestao.DisciplinaDTO(disciplina));
            linha.setFrequencia1(frequencia == null ? null : frequencia.getFrequencia1());
            linha.setFrequencia2(frequencia == null ? null : frequencia.getFrequencia2());
            linha.setFrequencia3(frequencia == null ? null : frequencia.getFrequencia3());
            linha.setFrequencia4(frequencia == null ? null : frequencia.getFrequencia4());
            linhas.add(linha);
        }

        linhas.sort(Comparator.comparing(item -> item.getAluno().getNome(), String.CASE_INSENSITIVE_ORDER));
        return linhas;
    }

    public FrequenciaDTO salvar(FrequenciaLancamentoDTO dto) {
        validar(dto);
        BoletimEntity boletim = boletimService.findById(dto.getBoletimId())
                .orElseThrow(() -> new IllegalArgumentException("Boletim nao encontrado."));
        DisciplinaEntity disciplina = disciplinaService.findById(dto.getDisciplina().getId())
                .orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));

        FrequenciaEntity entity = dto.getFrequenciaId() == null
                ? new FrequenciaEntity()
                : frequenciaRepository.findById(dto.getFrequenciaId()).orElseThrow(() -> new IllegalArgumentException("Frequencia nao encontrada."));

        entity.setBoletim(boletim);
        entity.setDisciplina(disciplina);
        entity.setFrequencia1(dto.getFrequencia1());
        entity.setFrequencia2(dto.getFrequencia2());
        entity.setFrequencia3(dto.getFrequencia3());
        entity.setFrequencia4(dto.getFrequencia4());

        FrequenciaEntity persisted = entity.getId() == null ? frequenciaRepository.save(entity) : frequenciaRepository.update(entity);
        return new FrequenciaDTO(persisted);
    }

    private void validar(FrequenciaLancamentoDTO dto) {
        if (dto == null || dto.getBoletimId() == null || dto.getDisciplina() == null || dto.getDisciplina().getId() == null) {
            throw new IllegalArgumentException("Lancamento de frequencia invalido.");
        }
        validarPercentual(dto.getFrequencia1());
        validarPercentual(dto.getFrequencia2());
        validarPercentual(dto.getFrequencia3());
        validarPercentual(dto.getFrequencia4());
    }

    private void validarPercentual(BigDecimal valor) {
        if (valor == null) {
            return;
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0 || valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("A frequencia deve estar entre 0 e 100.");
        }
    }
}
