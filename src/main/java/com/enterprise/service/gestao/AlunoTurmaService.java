package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.AlunoTurmaEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.repository.gestao.AlunoTurmaRepository;
import com.enterprise.service.admin.SituacaoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;

@RequestScoped
public class AlunoTurmaService {

    @Inject
    private AlunoTurmaRepository repository;
    @Inject
    private AlunoService alunoService;
    @Inject
    private TurmaService turmaService;
    @Inject
    private SituacaoService situacaoService;

    public List<AlunoTurmaDTO> listarDTO() {
        return repository.findAll().stream().map(AlunoTurmaDTO::new).toList();
    }

    public List<AlunoTurmaDTO> listarPorFiltros(Long alunoId, Long turmaId, Long situacaoId) {
        return repository.findByFilters(alunoId, turmaId, situacaoId).stream().map(AlunoTurmaDTO::new).toList();
    }

    public AlunoTurmaDTO matricular(Long alunoId, Long turmaId) {
        validar(alunoId, turmaId);

        AlunoEntity aluno = alunoService.findById(alunoId).orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado."));
        TurmaEntity turma = turmaService.findById(turmaId).orElseThrow(() -> new IllegalArgumentException("Turma nao encontrada."));
        SituacaoEntity situacao = situacaoService.findBySituacao("ATIVO").orElseThrow(() -> new IllegalStateException("Situacao ATIVO nao encontrada."));

        AlunoTurmaEntity entity = new AlunoTurmaEntity();
        entity.setAluno(aluno);
        entity.setTurma(turma);
        entity.setSituacao(situacao);

        repository.save(entity);
        return new AlunoTurmaDTO(entity);
    }

    public AlunoTurmaDTO atualizar(AlunoTurmaDTO dto) {
        if (dto == null || dto.getId() == null || dto.getAluno() == null || dto.getTurma() == null || dto.getSituacao() == null) {
            throw new IllegalArgumentException("Matricula invalida para atualizacao.");
        }

        Long alunoId = dto.getAluno().getId();
        Long turmaId = dto.getTurma().getId();
        Long situacaoId = dto.getSituacao().getId();

        if (alunoId == null || turmaId == null || situacaoId == null) {
            throw new IllegalArgumentException("Aluno, turma e situacao sao obrigatorios.");
        }
        if (repository.existsByAlunoAndTurmaExcludingId(alunoId, turmaId, dto.getId())) {
            throw new IllegalArgumentException("Aluno ja matriculado nesta turma.");
        }

        AlunoEntity aluno = alunoService.findById(alunoId).orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado."));
        TurmaEntity turma = turmaService.findById(turmaId).orElseThrow(() -> new IllegalArgumentException("Turma nao encontrada."));
        SituacaoEntity situacao = situacaoService.findById(situacaoId).orElseThrow(() -> new IllegalArgumentException("Situacao nao encontrada."));

        AlunoTurmaEntity entity = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Matricula nao encontrada."));
        entity.setAluno(aluno);
        entity.setTurma(turma);
        entity.setSituacao(situacao);

        return new AlunoTurmaDTO(repository.update(entity));
    }

    private void validar(Long alunoId, Long turmaId) {
        if (alunoId == null || turmaId == null) {
            throw new IllegalArgumentException("Aluno e turma sao obrigatorios.");
        }
        if (repository.existsByAlunoAndTurma(alunoId, turmaId)) {
            throw new IllegalArgumentException("Aluno ja matriculado nesta turma.");
        }
    }
}
