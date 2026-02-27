package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.AlunoTurmaEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.repository.gestao.AlunoTurmaRepository;
import com.enterprise.service.admin.SituacaoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AlunoTurmaService {

    @Inject
    private AlunoTurmaRepository repository;
    @Inject
    private AlunoService alunoService;
    @Inject
    private TurmaService turmaService;
    @Inject
    private SituacaoService situacaoService;

    public List<AlunoTurmaDTO> listarRecentesDTO(int limite) {
        return repository.findRecent(limite).stream().map(AlunoTurmaDTO::new).toList();
    }

    @Transactional
    public AlunoTurmaDTO matricular(Long alunoId, Long turmaId) {
        if (alunoId == null || turmaId == null) {
            throw new IllegalArgumentException("Aluno e turma sao obrigatorios.");
        }

        if (repository.existsByAlunoAndTurma(alunoId, turmaId)) {
            throw new IllegalArgumentException("Aluno ja matriculado nesta turma.");
        }

        AlunoEntity aluno = alunoService.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado."));
        TurmaEntity turma = turmaService.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma nao encontrada."));
        SituacaoEntity situacao = situacaoService.findBySituacao("ATIVO")
                .orElseThrow(() -> new IllegalStateException("Situacao ATIVO nao encontrada."));

        AlunoTurmaEntity entity = new AlunoTurmaEntity();
        entity.setAluno(aluno);
        entity.setTurma(turma);
        entity.setSituacao(situacao);

        repository.save(entity);
        return new AlunoTurmaDTO(entity);
    }
}
