package com.enterprise.repository.gestao;

import com.enterprise.model.entity.gestao.AlunoTurmaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class AlunoTurmaRepository {

    @PersistenceContext
    private EntityManager em;

    public AlunoTurmaEntity save(AlunoTurmaEntity entity) {
        em.persist(entity);
        return entity;
    }

    public List<AlunoTurmaEntity> findRecent(int limit) {
        return em.createQuery(
                        "select at from AlunoTurmaEntity at " +
                                "left join fetch at.aluno " +
                                "left join fetch at.turma " +
                                "left join fetch at.situacao " +
                                "order by at.id desc",
                        AlunoTurmaEntity.class
                )
                .setMaxResults(Math.max(1, limit))
                .getResultList();
    }

    public boolean existsByAlunoAndTurma(Long alunoId, Long turmaId) {
        Long total = em.createQuery(
                        "select count(at) from AlunoTurmaEntity at " +
                                "where at.aluno.id = :alunoId and at.turma.id = :turmaId",
                        Long.class
                )
                .setParameter("alunoId", alunoId)
                .setParameter("turmaId", turmaId)
                .getSingleResult();
        return total != null && total > 0;
    }
}
