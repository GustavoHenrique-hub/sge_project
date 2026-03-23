package com.enterprise.repository.gestao;

import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DisciplinaRepository {

    @PersistenceContext
    private EntityManager em;

    public DisciplinaEntity save(DisciplinaEntity entity) {
        em.persist(entity);
        return entity;
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
        em.remove(entity);
    }
}
