package com.enterprise.service.admin;

import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.repository.admin.SituacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SituacaoService {

    @Inject
    private SituacaoRepository repository;

    public SituacaoEntity criar(SituacaoEntity entity) {
        return repository.save(entity);
    }

    public Optional<SituacaoEntity> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<SituacaoEntity> findBySituacao(String situacao) {
        return repository.findBySituacao(situacao);
    }

    public List<SituacaoEntity> findByFilters(String situacao) {
        return repository.findByFilters(situacao);
    }

    public List<SituacaoEntity> findAll() {
        return repository.findAll();
    }

}
