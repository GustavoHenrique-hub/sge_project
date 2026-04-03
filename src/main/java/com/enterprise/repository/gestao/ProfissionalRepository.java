package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class ProfissionalRepository {

    @Inject
    private EntityManager em;

    public ProfissionalEntity save(ProfissionalEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }

    public ProfissionalEntity update(ProfissionalEntity entity) {
        return JpaTransaction.execute(em, () -> em.merge(entity));
    }

    public List<ProfissionalEntity> findAll() {
        return em.createQuery("select p from ProfissionalEntity p order by p.nome", ProfissionalEntity.class)
                .getResultList();
    }

    public List<ProfissionalEntity> findByFilters(String nome, String cpf) {
        String cpfNormalizado = cpf == null ? null : cpf.replaceAll("\\D", "");
        StringBuilder jpql = new StringBuilder("select p from ProfissionalEntity p where 1=1");
        if (nome != null && !nome.isBlank()) {
            jpql.append(" and lower(p.nome) like :nome");
        }
        if (cpfNormalizado != null && !cpfNormalizado.isBlank()) {
            jpql.append(" and p.cpf like :cpf");
        }
        jpql.append(" order by p.nome");

        TypedQuery<ProfissionalEntity> query = em.createQuery(jpql.toString(), ProfissionalEntity.class);
        if (nome != null && !nome.isBlank()) {
            query.setParameter("nome", "%" + nome.trim().toLowerCase() + "%");
        }
        if (cpfNormalizado != null && !cpfNormalizado.isBlank()) {
            query.setParameter("cpf", "%" + cpfNormalizado.trim() + "%");
        }
        return query.getResultList();
    }

    public Optional<ProfissionalEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select p from ProfissionalEntity p where p.id = :id",
                        ProfissionalEntity.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}

