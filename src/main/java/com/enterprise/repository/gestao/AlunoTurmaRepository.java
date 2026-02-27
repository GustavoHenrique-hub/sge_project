package com.enterprise.repository.gestao;

import com.enterprise.model.entity.gestao.AlunoTurmaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AlunoTurmaRepository {

    @PersistenceContext
    private EntityManager em;

    public AlunoTurmaEntity save(AlunoTurmaEntity entity) {
        em.persist(entity);
        return entity;
    }

    public Optional<AlunoTurmaEntity> findById(Long id) {
        return Optional.ofNullable(em.find(AlunoTurmaEntity.class, id));
    }

    public List<AlunoTurmaEntity> findAll() {
        return em.createQuery(
                "select at from AlunoTurmaEntity at " +
                        "left join fetch at.aluno " +
                        "left join fetch at.turma " +
                        "left join fetch at.situacao " +
                        "order by at.id desc",
                AlunoTurmaEntity.class
        ).getResultList();
    }

    public List<AlunoTurmaEntity> findRecent(int limit) {
        return em.createQuery(
                        "select at from AlunoTurmaEntity at " +
                                "left join fetch at.aluno " +
                                "left join fetch at.turma " +
                                "left join fetch at.situacao " +
                                "order by at.id asc",
                        AlunoTurmaEntity.class
                )
                .setMaxResults(Math.max(1, limit))
                .getResultList();
    }

    public List<AlunoTurmaEntity> findByFilters(Long alunoId, Long turmaId, Long situacaoId) {
        StringBuilder jpql = new StringBuilder(
                "select at from AlunoTurmaEntity at " +
                        "left join fetch at.aluno " +
                        "left join fetch at.turma " +
                        "left join fetch at.situacao " +
                        "where 1=1"
        );
        if (alunoId != null) {
            jpql.append(" and at.aluno.id = :alunoId");
        }
        if (turmaId != null) {
            jpql.append(" and at.turma.id = :turmaId");
        }
        if (situacaoId != null) {
            jpql.append(" and at.situacao.id = :situacaoId");
        }
        jpql.append(" order by at.id desc");

        TypedQuery<AlunoTurmaEntity> query = em.createQuery(jpql.toString(), AlunoTurmaEntity.class);
        if (alunoId != null) {
            query.setParameter("alunoId", alunoId);
        }
        if (turmaId != null) {
            query.setParameter("turmaId", turmaId);
        }
        if (situacaoId != null) {
            query.setParameter("situacaoId", situacaoId);
        }
        return query.getResultList();
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
