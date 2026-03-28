package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.ProfessorDisciplinaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.ProfessorEntity;
import com.enterprise.model.entity.gestao.ProfessorDisciplinaEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.repository.gestao.ProfessorDisciplinaRepository;
import com.enterprise.service.admin.SituacaoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ProfessorDisciplinaService {

    @Inject
    private ProfessorDisciplinaRepository repository;
    @Inject
    private ProfessorService professorService;
    @Inject
    private DisciplinaService disciplinaService;
    @Inject
    private SituacaoService situacaoService;

    public List<ProfessorDisciplinaDTO> listarDTO() {
        return repository.findAll().stream().map(ProfessorDisciplinaDTO::new).toList();
    }

    public List<ProfessorDisciplinaDTO> listarPorFiltros(Long professorId, Long disciplinaId, Long situacaoId) {
        return repository.findByFilters(professorId, disciplinaId, situacaoId).stream().map(ProfessorDisciplinaDTO::new).toList();
    }

    @Transactional
    public ProfessorDisciplinaDTO vincular(Long professorId, Long disciplinaId) {
        validar(professorId, disciplinaId);

        ProfessorEntity professor = professorService.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor nao encontrado."));
        DisciplinaEntity disciplina = disciplinaService.findById(disciplinaId).orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));
        SituacaoEntity situacao = situacaoService.findBySituacao("ATIVO").orElseThrow(() -> new IllegalStateException("Situacao ATIVO nao encontrada."));

        ProfessorDisciplinaEntity entity = new ProfessorDisciplinaEntity();
        entity.setProfessor(professor);
        entity.setDisciplina(disciplina);
        entity.setSituacao(situacao);

        repository.save(entity);
        return new ProfessorDisciplinaDTO(entity);
    }

    @Transactional
    public ProfessorDisciplinaDTO atualizar(ProfessorDisciplinaDTO dto) {
        if (dto == null || dto.getId() == null || dto.getProfessor() == null || dto.getDisciplina() == null || dto.getSituacao() == null) {
            throw new IllegalArgumentException("Vinculo invalido para atualizacao.");
        }

        Long professorId = dto.getProfessor().getId();
        Long disciplinaId = dto.getDisciplina().getId();
        Long situacaoId = dto.getSituacao().getId();

        if (professorId == null || disciplinaId == null || situacaoId == null) {
            throw new IllegalArgumentException("Professor, disciplina e situacao sao obrigatorios.");
        }
        if (repository.existsByProfessorAndDisciplinaExcludingId(professorId, disciplinaId, dto.getId())) {
            throw new IllegalArgumentException("Professor ja matriculado nesta disciplina.");
        }

        ProfessorEntity professor = professorService.findById(professorId).orElseThrow(() -> new IllegalArgumentException("Professor nao encontrado."));
        DisciplinaEntity disciplina = disciplinaService.findById(disciplinaId).orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));
        SituacaoEntity situacao = situacaoService.findById(situacaoId).orElseThrow(() -> new IllegalArgumentException("Situacao nao encontrada."));

        ProfessorDisciplinaEntity entity = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Vinculo nao encontrado."));
        entity.setProfessor(professor);
        entity.setDisciplina(disciplina);
        entity.setSituacao(situacao);

        return new ProfessorDisciplinaDTO(repository.update(entity));
    }

    private void validar(Long professorId, Long disciplinaId) {
        if (professorId == null || disciplinaId == null) {
            throw new IllegalArgumentException("Professor e disciplina são obrigatorios.");
        }
        if (repository.existsByProfessorAndDisciplina(professorId, disciplinaId)) {
            throw new IllegalArgumentException("Professor ja matriculado nesta disciplina.");
        }
    }
}
