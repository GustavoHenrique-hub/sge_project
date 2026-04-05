package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.AlunoTurmaEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a AlunoTurmaRepository.
 */

@RequestScoped
public class AlunoTurmaRepository {

    @Inject
    private EntityManager em;
    /**
     * Persiste um novo registro no banco dentro do controle transacional da aplicacao.
     */

    public AlunoTurmaEntity save(AlunoTurmaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            em.persist(entity);
            return entity;
        });
    }
    /**
     * Mescla e salva as alteracoes de um registro ja existente no banco de dados.
     */

    public AlunoTurmaEntity update(AlunoTurmaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            return em.merge(entity);
        });
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<AlunoTurmaEntity> findById(Long id) {
        return Optional.ofNullable(em.find(AlunoTurmaEntity.class, id));
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

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
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

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
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

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
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public Optional<AlunoTurmaEntity> findByAlunoAndTurma(Long alunoId, Long turmaId) {
        return em.createQuery(
                        "select at from AlunoTurmaEntity at " +
                                "left join fetch at.aluno " +
                                "left join fetch at.turma " +
                                "left join fetch at.situacao " +
                                "where at.aluno.id = :alunoId and at.turma.id = :turmaId " +
                                "order by at.id desc",
                        AlunoTurmaEntity.class
                )
                .setParameter("alunoId", alunoId)
                .setParameter("turmaId", turmaId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public List<AlunoTurmaEntity> findAtivosByAluno(Long alunoId) {
        return em.createQuery(
                        "select at from AlunoTurmaEntity at " +
                                "left join fetch at.aluno " +
                                "left join fetch at.turma " +
                                "left join fetch at.situacao " +
                                "where at.aluno.id = :alunoId and upper(at.situacao.situacao) = :situacao " +
                                "order by at.id desc",
                        AlunoTurmaEntity.class
                )
                .setParameter("alunoId", alunoId)
                .setParameter("situacao", "ATIVO")
                .getResultList();
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public boolean existsAtivoByAluno(Long alunoId) {
        Long total = em.createQuery(
                        "select count(at) from AlunoTurmaEntity at " +
                                "where at.aluno.id = :alunoId and upper(at.situacao.situacao) = :situacao",
                        Long.class
                )
                .setParameter("alunoId", alunoId)
                .setParameter("situacao", "ATIVO")
                .getSingleResult();
        return total != null && total > 0;
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

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
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public boolean existsByAlunoAndTurmaExcludingId(Long alunoId, Long turmaId, Long id) {
        Long total = em.createQuery(
                        "select count(at) from AlunoTurmaEntity at " +
                                "where at.aluno.id = :alunoId and at.turma.id = :turmaId and at.id <> :id",
                        Long.class
                )
                .setParameter("alunoId", alunoId)
                .setParameter("turmaId", turmaId)
                .setParameter("id", id == null ? -1L : id)
                .getSingleResult();
        return total != null && total > 0;
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    private void attachReferences(AlunoTurmaEntity entity) {
        if (entity.getAluno() != null && entity.getAluno().getId() != null) {
            entity.setAluno(em.getReference(AlunoEntity.class, entity.getAluno().getId()));
        }
        if (entity.getTurma() != null && entity.getTurma().getId() != null) {
            entity.setTurma(em.getReference(TurmaEntity.class, entity.getTurma().getId()));
        }
        if (entity.getSituacao() != null && entity.getSituacao().getId() != null) {
            entity.setSituacao(em.getReference(SituacaoEntity.class, entity.getSituacao().getId()));
        }
    }
}
