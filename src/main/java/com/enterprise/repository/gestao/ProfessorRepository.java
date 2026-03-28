package com.enterprise.repository.gestao;

import com.enterprise.model.entity.gestao.ProfessorEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProfessorRepository {

    @PersistenceContext
    private EntityManager em;

    public ProfessorEntity save(ProfessorEntity entity) {
        em.persist(entity);
        return entity;
    }

    public ProfessorEntity update(ProfessorEntity entity) {
        return em.merge(entity);
    }

    public List<ProfessorEntity> findAll(){
        return em.createQuery("select p from ProfessorEntity p order by p.nome", ProfessorEntity.class)
                .getResultList();
    }

    public List<ProfessorEntity> findByFilters(String nome, String cpf) {
        StringBuilder jpql = new StringBuilder("select p from ProfessorEntity p where 1=1");
        if (nome != null && !nome.isBlank()) {
            jpql.append(" and lower(p.nome) like :nome");
        }
        if (cpf != null && !cpf.isBlank()) {
            jpql.append(" and p.cpf like :cpf");
        }
        jpql.append(" order by p.nome");

        TypedQuery<ProfessorEntity> query = em.createQuery(jpql.toString(), ProfessorEntity.class);
        if (nome != null && !nome.isBlank()) {
            query.setParameter("nome", "%" + nome.trim().toLowerCase() + "%");
        }
        if (cpf != null && !cpf.isBlank()) {
            query.setParameter("cpf", "%" + cpf.trim() + "%");
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
