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

    public TurmaEntity update(TurmaEntity entity) {
        return em.merge(entity);
    }

    public Optional<TurmaEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select t from TurmaEntity t where t.id = :id",
                        TurmaEntity.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public List<TurmaEntity> findAll() {
        return em.createQuery("select t from TurmaEntity t order by t.turma", TurmaEntity.class)
                .getResultList();
    }

    public void remove(TurmaEntity entity) {
        em.remove(entity);
    }
}
