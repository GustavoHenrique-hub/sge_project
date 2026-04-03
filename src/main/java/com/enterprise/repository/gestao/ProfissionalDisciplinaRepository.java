package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import com.enterprise.model.entity.gestao.ProfissionalDisciplinaEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class ProfissionalDisciplinaRepository {

    @Inject
    private EntityManager em;

    public ProfissionalDisciplinaEntity save(ProfissionalDisciplinaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            em.persist(entity);
            return entity;
        });
    }

    public ProfissionalDisciplinaEntity update(ProfissionalDisciplinaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            return em.merge(entity);
        });
    }

    public Optional<ProfissionalDisciplinaEntity> findById(Long id) {
        return Optional.ofNullable(em.find(ProfissionalDisciplinaEntity.class, id));
    }

    public List<ProfissionalDisciplinaEntity> findAll() {
        return em.createQuery(
                "select pd from ProfissionalDisciplinaEntity pd " +
                        "left join fetch pd.profissional " +
                        "left join fetch pd.disciplina " +
                        "left join fetch pd.situacao " +
                        "order by pd.id desc",
                ProfissionalDisciplinaEntity.class
        ).getResultList();
    }

    public List<ProfissionalDisciplinaEntity> findRecent(int limit) {
        return em.createQuery(
                        "select pd from ProfissionalDisciplinaEntity pd " +
                                "left join fetch pd.profissional " +
                                "left join fetch pd.disciplina " +
                                "left join fetch pd.situacao " +
                                "order by pd.id asc",
                        ProfissionalDisciplinaEntity.class
                )
                .setMaxResults(Math.max(1, limit))
                .getResultList();
    }

    public List<ProfissionalDisciplinaEntity> findByFilters(Long profissionalId, Long disciplinaId, Long situacaoId) {
        StringBuilder jpql = new StringBuilder(
                "select pd from ProfissionalDisciplinaEntity pd " +
                        "left join fetch pd.profissional " +
                        "left join fetch pd.disciplina " +
                        "left join fetch pd.situacao " +
                        "where 1=1"
        );
        if (profissionalId != null) {
            jpql.append(" and pd.profissional.id = :profissionalId");
        }
        if (disciplinaId != null) {
            jpql.append(" and pd.disciplina.id = :disciplinaId");
        }
        if (situacaoId != null) {
            jpql.append(" and pd.situacao.id = :situacaoId");
        }
        jpql.append(" order by pd.id desc");

        TypedQuery<ProfissionalDisciplinaEntity> query = em.createQuery(jpql.toString(), ProfissionalDisciplinaEntity.class);
        if (profissionalId != null) {
            query.setParameter("profissionalId", profissionalId);
        }
        if (disciplinaId != null) {
            query.setParameter("disciplinaId", disciplinaId);
        }
        if (situacaoId != null) {
            query.setParameter("situacaoId", situacaoId);
        }
        return query.getResultList();
    }

    public boolean existsByProfissionalAndDisciplina(Long profissionalId, Long disciplinaId) {
        Long total = em.createQuery(
                        "select count(at) from ProfissionalDisciplinaEntity at " +
                                "where at.profissional.id = :profissionalId and at.disciplina.id = :disciplinaId",
                        Long.class
                )
                .setParameter("profissionalId", profissionalId)
                .setParameter("disciplinaId", disciplinaId)
                .getSingleResult();
        return total != null && total > 0;
    }

    public boolean existsByProfissionalAndDisciplinaExcludingId(Long profissionalId, Long disciplinaId, Long id) {
        Long total = em.createQuery(
                        "select count(at) from ProfissionalDisciplinaEntity at " +
                                "where at.profissional.id = :profissionalId and at.disciplina.id = :disciplinaId and at.id <> :id",
                        Long.class
                )
                .setParameter("profissionalId", profissionalId)
                .setParameter("disciplinaId", disciplinaId)
                .setParameter("id", id == null ? -1L : id)
                .getSingleResult();
        return total != null && total > 0;
    }

    private void attachReferences(ProfissionalDisciplinaEntity entity) {
        if (entity.getProfissional() != null && entity.getProfissional().getId() != null) {
            entity.setProfissional(em.getReference(ProfissionalEntity.class, entity.getProfissional().getId()));
        }
        if (entity.getDisciplina() != null && entity.getDisciplina().getId() != null) {
            entity.setDisciplina(em.getReference(DisciplinaEntity.class, entity.getDisciplina().getId()));
        }
        if (entity.getSituacao() != null && entity.getSituacao().getId() != null) {
            entity.setSituacao(em.getReference(SituacaoEntity.class, entity.getSituacao().getId()));
        }
    }
}

