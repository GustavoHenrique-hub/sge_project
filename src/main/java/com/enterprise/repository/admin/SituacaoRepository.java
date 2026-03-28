package com.enterprise.repository.admin;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.admin.SituacaoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SituacaoRepository {

    @Inject
    private EntityManager em;

    public SituacaoEntity save(SituacaoEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }

    public Optional<SituacaoEntity> findById(Long id) {
        return Optional.ofNullable(em.find(SituacaoEntity.class, id));
    }

    public List<SituacaoEntity> findAll() {
        return em.createQuery("select s from SituacaoEntity s order by s.id", SituacaoEntity.class)
                .getResultList();
    }

    public List<SituacaoEntity> findByFilters(String situacao) {
        StringBuilder jpql = new StringBuilder("select s from SituacaoEntity s where 1=1");
        if (situacao != null && !situacao.isBlank()) {
            jpql.append(" and lower(s.situacao) like :situacao");
        }
        jpql.append(" order by s.situacao");

        TypedQuery<SituacaoEntity> query = em.createQuery(jpql.toString(), SituacaoEntity.class);
        if (situacao != null && !situacao.isBlank()) {
            query.setParameter("situacao", "%" + situacao.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public Optional<SituacaoEntity> findBySituacao(String situacao) {
        if (situacao == null || situacao.isBlank()) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select s from SituacaoEntity s where upper(s.situacao) = :situacao",
                        SituacaoEntity.class
                )
                .setParameter("situacao", situacao.trim().toUpperCase())
                .getResultStream()
                .findFirst();
    }
}
