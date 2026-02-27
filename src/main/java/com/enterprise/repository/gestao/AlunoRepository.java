package com.enterprise.repository.gestao;

import com.enterprise.model.entity.gestao.AlunoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AlunoRepository {

    @PersistenceContext
    private EntityManager em;

    public AlunoEntity save(AlunoEntity entity) {
        em.persist(entity);
        return entity;
    }

    public List<AlunoEntity> findAll(){
    return em.createQuery("select p from AlunoEntity p order by p.nome", AlunoEntity.class)
            .getResultList();
    }

    public List<AlunoEntity> findByFilters(String nome, String cpf) {
        StringBuilder jpql = new StringBuilder("select a from AlunoEntity a where 1=1");
        if (nome != null && !nome.isBlank()) {
            jpql.append(" and lower(a.nome) like :nome");
        }
        if (cpf != null && !cpf.isBlank()) {
            jpql.append(" and a.cpf like :cpf");
        }
        jpql.append(" order by a.nome");

        TypedQuery<AlunoEntity> query = em.createQuery(jpql.toString(), AlunoEntity.class);
        if (nome != null && !nome.isBlank()) {
            query.setParameter("nome", "%" + nome.trim().toLowerCase() + "%");
        }
        if (cpf != null && !cpf.isBlank()) {
            query.setParameter("cpf", "%" + cpf.trim() + "%");
        }
        return query.getResultList();
    }

    public Optional<AlunoEntity> findById(Long id) {
        return Optional.ofNullable(em.find(AlunoEntity.class, id));
    }
}
