package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class DisciplinaRepository {

    @Inject
    private EntityManager em;

    public DisciplinaEntity save(DisciplinaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }

    public DisciplinaEntity update(DisciplinaEntity entity) {
        return JpaTransaction.execute(em, () -> em.merge(entity));
    }

    public Optional<DisciplinaEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select d from DisciplinaEntity d where d.id = :id",
                        DisciplinaEntity.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public List<DisciplinaEntity> findAll() {
        return em.createQuery("select d from DisciplinaEntity d order by d.descricao", DisciplinaEntity.class)
                .getResultList();
    }

    public void remove(DisciplinaEntity entity) {
        JpaTransaction.run(em, () -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }
}
