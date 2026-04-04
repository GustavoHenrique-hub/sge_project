package com.enterprise.service.academico;

import com.enterprise.dto.academico.BoletimDTO;
import com.enterprise.dto.academico.NotaDTO;
import com.enterprise.dto.academico.NotaLancamentoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.academico.BoletimEntity;
import com.enterprise.model.entity.academico.NotaEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.enums.academico.ConceitoNota;
import com.enterprise.repository.academico.NotaRepository;
import com.enterprise.service.gestao.AlunoTurmaService;
import com.enterprise.service.gestao.DisciplinaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequestScoped
public class NotaService {

    @Inject
    private NotaRepository notaRepository;
    @Inject
    private DisciplinaService disciplinaService;
    @Inject
    private AlunoTurmaService alunoTurmaService;
    @Inject
    private BoletimService boletimService;

    public List<NotaLancamentoDTO> listarPorTurmaEDisciplina(Long turmaId, Long disciplinaId) {
        if (turmaId == null || disciplinaId == null) {
            throw new IllegalArgumentException("Turma e disciplina sao obrigatorias.");
        }

        DisciplinaEntity disciplina = disciplinaService.findById(disciplinaId)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));

        List<NotaLancamentoDTO> linhas = new ArrayList<>();
        for (AlunoTurmaDTO matricula : alunoTurmaService.listarPorFiltros(null, turmaId, null)) {
            BoletimDTO boletim = boletimService.obterOuCriar(matricula.getAluno().getId(), turmaId);
            NotaDTO nota = notaRepository.findByBoletimAndDisciplina(boletim.getId(), disciplinaId)
                    .map(NotaDTO::new)
                    .orElse(null);

            NotaLancamentoDTO linha = new NotaLancamentoDTO();
            linha.setBoletimId(boletim.getId());
            linha.setNotaId(nota == null ? null : nota.getId());
            linha.setAluno(matricula.getAluno());
            linha.setTurma(matricula.getTurma());
            linha.setDisciplina(new com.enterprise.dto.gestao.DisciplinaDTO(disciplina));
            linha.setNota1(nota == null ? null : nota.getNota1());
            linha.setNota2(nota == null ? null : nota.getNota2());
            linha.setNota3(nota == null ? null : nota.getNota3());
            linha.setNota4(nota == null ? null : nota.getNota4());
            linhas.add(linha);
        }

        linhas.sort(Comparator.comparing(item -> item.getAluno().getNome(), String.CASE_INSENSITIVE_ORDER));
        return linhas;
    }

    public NotaDTO salvar(NotaLancamentoDTO dto) {
        validar(dto);
        BoletimEntity boletim = boletimService.findById(dto.getBoletimId())
                .orElseThrow(() -> new IllegalArgumentException("Boletim nao encontrado."));
        DisciplinaEntity disciplina = disciplinaService.findById(dto.getDisciplina().getId())
                .orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));

        NotaEntity entity = dto.getNotaId() == null
                ? new NotaEntity()
                : notaRepository.findById(dto.getNotaId()).orElseThrow(() -> new IllegalArgumentException("Nota nao encontrada."));

        entity.setBoletim(boletim);
        entity.setDisciplina(disciplina);
        entity.setNota1(ConceitoNota.fromValue(dto.getNota1()));
        entity.setNota2(ConceitoNota.fromValue(dto.getNota2()));
        entity.setNota3(ConceitoNota.fromValue(dto.getNota3()));
        entity.setNota4(ConceitoNota.fromValue(dto.getNota4()));

        NotaEntity persisted = entity.getId() == null ? notaRepository.save(entity) : notaRepository.update(entity);
        return new NotaDTO(persisted);
    }

    public List<String> listarConceitos() {
        return List.of("I", "R", "B", "MB");
    }

    private void validar(NotaLancamentoDTO dto) {
        if (dto == null || dto.getBoletimId() == null || dto.getDisciplina() == null || dto.getDisciplina().getId() == null) {
            throw new IllegalArgumentException("Lancamento de nota invalido.");
        }
        ConceitoNota.fromValue(dto.getNota1());
        ConceitoNota.fromValue(dto.getNota2());
        ConceitoNota.fromValue(dto.getNota3());
        ConceitoNota.fromValue(dto.getNota4());
    }
}
