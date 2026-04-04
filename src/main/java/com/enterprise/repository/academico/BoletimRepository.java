package com.enterprise.repository.academico;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.academico.BoletimEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class BoletimRepository {

    @Inject
    private EntityManager em;

    public BoletimEntity save(BoletimEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            em.persist(entity);
            return entity;
        });
    }

    public BoletimEntity update(BoletimEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            return em.merge(entity);
        });
    }

    public Optional<BoletimEntity> findById(Long id) {
        return Optional.ofNullable(em.find(BoletimEntity.class, id));
    }

    public Optional<BoletimEntity> findByAlunoAndTurma(Long alunoId, Long turmaId) {
        return em.createQuery(
                        "select b from BoletimEntity b " +
                                "where b.aluno.id = :alunoId and b.turma.id = :turmaId " +
                                "order by b.id desc",
                        BoletimEntity.class
                )
                .setParameter("alunoId", alunoId)
                .setParameter("turmaId", turmaId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public List<BoletimEntity> findByAluno(Long alunoId) {
        return em.createQuery(
                        "select b from BoletimEntity b " +
                                "left join fetch b.aluno " +
                                "left join fetch b.turma " +
                                "where b.aluno.id = :alunoId " +
                                "order by b.turma.turma, b.id",
                        BoletimEntity.class
                )
                .setParameter("alunoId", alunoId)
                .getResultList();
    }

    private void attachReferences(BoletimEntity entity) {
        if (entity.getAluno() != null && entity.getAluno().getId() != null) {
            entity.setAluno(em.getReference(AlunoEntity.class, entity.getAluno().getId()));
        }
        if (entity.getTurma() != null && entity.getTurma().getId() != null) {
            entity.setTurma(em.getReference(TurmaEntity.class, entity.getTurma().getId()));
        }
    }
}
