package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.gestao.TurmaEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class TurmaRepository {

    @Inject
    private EntityManager em;

    public TurmaEntity save(TurmaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }

    public TurmaEntity update(TurmaEntity entity) {
        return JpaTransaction.execute(em, () -> em.merge(entity));
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
        JpaTransaction.run(em, () -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }
}
