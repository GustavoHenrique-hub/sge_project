package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.TurmaDTO;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.repository.gestao.TurmaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TurmaService {

    @Inject
    private TurmaRepository repository;

    @Transactional
    public TurmaDTO criar(TurmaDTO dto) {
        if (dto == null || dto.getTurma() == null || dto.getTurma().isBlank()) {
            throw new IllegalArgumentException("Turma e obrigatoria.");
        }
        TurmaEntity entity = new TurmaEntity(dto);
        repository.save(entity);
        return new TurmaDTO(entity);
    }

    public List<TurmaEntity> findAll() {
        return repository.findAll();
    }

    public Optional<TurmaEntity> findById(Long id) {
        return repository.findById(id);
    }
}