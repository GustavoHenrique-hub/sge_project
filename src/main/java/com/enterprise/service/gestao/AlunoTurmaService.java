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

    private static final String SITUACAO_ATIVO = "ATIVO";
    private static final String SITUACAO_INATIVO = "INATIVO";

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

    public boolean possuiMatriculaAtiva(Long alunoId) {
        if (alunoId == null) {
            return false;
        }
        return repository.existsAtivoByAluno(alunoId);
    }

    public AlunoTurmaDTO matricular(Long alunoId, Long turmaId) {
        validarIds(alunoId, turmaId);

        AlunoEntity aluno = alunoService.findById(alunoId).orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado."));
        TurmaEntity turma = turmaService.findById(turmaId).orElseThrow(() -> new IllegalArgumentException("Turma nao encontrada."));
        SituacaoEntity situacaoAtiva = situacaoService.findBySituacao(SITUACAO_ATIVO)
                .orElseThrow(() -> new IllegalStateException("Situacao ATIVO nao encontrada."));
        SituacaoEntity situacaoInativa = situacaoService.findBySituacao(SITUACAO_INATIVO)
                .orElseThrow(() -> new IllegalStateException("Situacao INATIVO nao encontrada."));

        AlunoTurmaEntity existenteNaTurma = repository.findByAlunoAndTurma(alunoId, turmaId).orElse(null);
        if (existenteNaTurma != null && isSituacao(existenteNaTurma.getSituacao(), SITUACAO_ATIVO)) {
            throw new IllegalArgumentException("Aluno ja possui matricula ativa nesta turma.");
        }

        inativarOutrasMatriculasAtivas(alunoId, null, situacaoInativa);

        if (existenteNaTurma != null) {
            existenteNaTurma.setAluno(aluno);
            existenteNaTurma.setTurma(turma);
            existenteNaTurma.setSituacao(situacaoAtiva);
            return new AlunoTurmaDTO(repository.update(existenteNaTurma));
        }

        AlunoTurmaEntity entity = new AlunoTurmaEntity();
        entity.setAluno(aluno);
        entity.setTurma(turma);
        entity.setSituacao(situacaoAtiva);

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

        if (isSituacao(situacao, SITUACAO_ATIVO)) {
            SituacaoEntity situacaoInativa = situacaoService.findBySituacao(SITUACAO_INATIVO)
                    .orElseThrow(() -> new IllegalStateException("Situacao INATIVO nao encontrada."));
            inativarOutrasMatriculasAtivas(alunoId, dto.getId(), situacaoInativa);
        }

        return new AlunoTurmaDTO(repository.update(entity));
    }

    private void validarIds(Long alunoId, Long turmaId) {
        if (alunoId == null || turmaId == null) {
            throw new IllegalArgumentException("Aluno e turma sao obrigatorios.");
        }
    }

    private void inativarOutrasMatriculasAtivas(Long alunoId, Long matriculaPreservadaId, SituacaoEntity situacaoInativa) {
        for (AlunoTurmaEntity matriculaAtiva : repository.findAtivosByAluno(alunoId)) {
            if (matriculaPreservadaId != null && matriculaPreservadaId.equals(matriculaAtiva.getId())) {
                continue;
            }
            matriculaAtiva.setSituacao(situacaoInativa);
            repository.update(matriculaAtiva);
        }
    }

    private boolean isSituacao(SituacaoEntity situacao, String valor) {
        return situacao != null
                && situacao.getSituacao() != null
                && situacao.getSituacao().equalsIgnoreCase(valor);
    }
}
