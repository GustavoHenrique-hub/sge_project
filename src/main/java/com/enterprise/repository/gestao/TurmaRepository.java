package com.enterprise.repository.gestao;

import com.enterprise.model.entity.gestao.TurmaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TurmaRepository {

    @PersistenceContext
    private EntityManager em;

    public TurmaEntity save(TurmaEntity entity) {
        em.persist(entity);
        return entity;
    }

    public Optional<TurmaEntity> findById(Long id) {
        return Optional.ofNullable(em.find(TurmaEntity.class, id));
    }

    public List<TurmaEntity> findAll() {
        return em.createQuery("select t from TurmaEntity t order by t.turma", TurmaEntity.class)
                .getResultList();
    }
}