package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.gestao.ProfessorEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class ProfessorRepository {

    @Inject
    private EntityManager em;

    public ProfessorEntity save(ProfessorEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }

    public ProfessorEntity update(ProfessorEntity entity) {
        return JpaTransaction.execute(em, () -> em.merge(entity));
    }

    public List<ProfessorEntity> findAll() {
        return em.createQuery("select p from ProfessorEntity p order by p.nome", ProfessorEntity.class)
                .getResultList();
    }

    public List<ProfessorEntity> findByFilters(String nome, String cpf) {
        String cpfNormalizado = cpf == null ? null : cpf.replaceAll("\\D", "");
        StringBuilder jpql = new StringBuilder("select p from ProfessorEntity p where 1=1");
        if (nome != null && !nome.isBlank()) {
            jpql.append(" and lower(p.nome) like :nome");
        }
        if (cpfNormalizado != null && !cpfNormalizado.isBlank()) {
            jpql.append(" and p.cpf like :cpf");
        }
        jpql.append(" order by p.nome");

        TypedQuery<ProfessorEntity> query = em.createQuery(jpql.toString(), ProfessorEntity.class);
        if (nome != null && !nome.isBlank()) {
            query.setParameter("nome", "%" + nome.trim().toLowerCase() + "%");
        }
        if (cpfNormalizado != null && !cpfNormalizado.isBlank()) {
            query.setParameter("cpf", "%" + cpfNormalizado.trim() + "%");
        }
        return query.getResultList();
    }

    public Optional<ProfessorEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select p from ProfessorEntity p where p.id = :id",
                        ProfessorEntity.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
