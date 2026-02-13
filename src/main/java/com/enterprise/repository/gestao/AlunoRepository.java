package com.enterprise.repository.gestao;

import com.enterprise.model.entity.gestao.AlunoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AlunoRepository {

    @PersistenceContext
    private EntityManager em;

    public List<AlunoEntity> findAll(){
    return em.createQuery("select p from AlunoEntity p order by p.id", AlunoEntity.class)
            .getResultList();
    }

    public Optional<AlunoEntity> findById(Long id) {
        return Optional.ofNullable(em.find(AlunoEntity.class, id));
    }
}
