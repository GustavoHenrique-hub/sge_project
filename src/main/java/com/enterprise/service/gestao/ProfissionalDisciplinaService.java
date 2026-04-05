package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.ProfissionalDisciplinaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import com.enterprise.model.entity.gestao.ProfissionalDisciplinaEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.repository.gestao.ProfissionalDisciplinaRepository;
import com.enterprise.service.admin.SituacaoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de ProfissionalDisciplinaService.
 */

@RequestScoped
public class ProfissionalDisciplinaService {

    @Inject
    private ProfissionalDisciplinaRepository repository;
    @Inject
    private ProfissionalService profissionalService;
    @Inject
    private DisciplinaService disciplinaService;
    @Inject
    private SituacaoService situacaoService;
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public List<ProfissionalDisciplinaDTO> listarDTO() {
        return repository.findAll().stream().map(ProfissionalDisciplinaDTO::new).toList();
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public List<ProfissionalDisciplinaDTO> listarPorFiltros(Long profissionalId, Long disciplinaId, Long situacaoId) {
        return repository.findByFilters(profissionalId, disciplinaId, situacaoId).stream().map(ProfissionalDisciplinaDTO::new).toList();
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public ProfissionalDisciplinaDTO vincular(Long profissionalId, Long disciplinaId) {
        validar(profissionalId, disciplinaId);

        ProfissionalEntity profissional = profissionalService.findById(profissionalId).orElseThrow(() -> new IllegalArgumentException("Profissional nao encontrado."));
        DisciplinaEntity disciplina = disciplinaService.findById(disciplinaId).orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));
        SituacaoEntity situacao = situacaoService.findBySituacao("ATIVO").orElseThrow(() -> new IllegalStateException("Situacao ATIVO nao encontrada."));

        ProfissionalDisciplinaEntity entity = new ProfissionalDisciplinaEntity();
        entity.setProfissional(profissional);
        entity.setDisciplina(disciplina);
        entity.setSituacao(situacao);

        repository.save(entity);
        return new ProfissionalDisciplinaDTO(entity);
    }
    /**
     * Atualiza o registro existente aplicando as regras de negocio desta camada.
     */

    public ProfissionalDisciplinaDTO atualizar(ProfissionalDisciplinaDTO dto) {
        if (dto == null || dto.getId() == null || dto.getProfissional() == null || dto.getDisciplina() == null || dto.getSituacao() == null) {
            throw new IllegalArgumentException("Vinculo invalido para atualizacao.");
        }

        Long profissionalId = dto.getProfissional().getId();
        Long disciplinaId = dto.getDisciplina().getId();
        Long situacaoId = dto.getSituacao().getId();

        if (profissionalId == null || disciplinaId == null || situacaoId == null) {
            throw new IllegalArgumentException("Profissional, disciplina e situacao sao obrigatorios.");
        }
        if (repository.existsByProfissionalAndDisciplinaExcludingId(profissionalId, disciplinaId, dto.getId())) {
            throw new IllegalArgumentException("Profissional ja matriculado nesta disciplina.");
        }

        ProfissionalEntity profissional = profissionalService.findById(profissionalId).orElseThrow(() -> new IllegalArgumentException("Profissional nao encontrado."));
        DisciplinaEntity disciplina = disciplinaService.findById(disciplinaId).orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));
        SituacaoEntity situacao = situacaoService.findById(situacaoId).orElseThrow(() -> new IllegalArgumentException("Situacao nao encontrada."));

        ProfissionalDisciplinaEntity entity = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Vinculo nao encontrado."));
        entity.setProfissional(profissional);
        entity.setDisciplina(disciplina);
        entity.setSituacao(situacao);

        return new ProfissionalDisciplinaDTO(repository.update(entity));
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private void validar(Long profissionalId, Long disciplinaId) {
        if (profissionalId == null || disciplinaId == null) {
            throw new IllegalArgumentException("Profissional e disciplina sÃƒÂ£o obrigatorios.");
        }
        if (repository.existsByProfissionalAndDisciplina(profissionalId, disciplinaId)) {
            throw new IllegalArgumentException("Profissional ja matriculado nesta disciplina.");
        }
    }
}

