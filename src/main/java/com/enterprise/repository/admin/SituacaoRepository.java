package com.enterprise.repository.admin;

import com.enterprise.model.entity.admin.SituacaoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SituacaoRepository {

    @PersistenceContext
    private EntityManager em;

    public SituacaoEntity save(SituacaoEntity entity) {
        em.persist(entity);
        return entity;
    }

    public Optional<SituacaoEntity> findById(Long id) {
        return Optional.ofNullable(em.find(SituacaoEntity.class, id));
    }

    public List<SituacaoEntity> findAll() {
        return em.createQuery("select s from SituacaoEntity s order by s.id", SituacaoEntity.class)
                .getResultList();
    }
}
