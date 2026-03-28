package com.enterprise.repository.gestao;

import com.enterprise.model.entity.gestao.ProfessorDisciplinaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProfessorDisciplinaRepository {

    @PersistenceContext
    private EntityManager em;

    public ProfessorDisciplinaEntity save(ProfessorDisciplinaEntity entity) {
        em.persist(entity);
        return entity;
    }

    public ProfessorDisciplinaEntity update(ProfessorDisciplinaEntity entity) {
        return em.merge(entity);
    }

    public Optional<ProfessorDisciplinaEntity> findById(Long id) {
        return Optional.ofNullable(em.find(ProfessorDisciplinaEntity.class, id));
    }

    public List<ProfessorDisciplinaEntity> findAll() {
        return em.createQuery(
                "select pd from ProfessorDisciplinaEntity pd " +
                        "left join fetch pd.professor " +
                        "left join fetch pd.disciplina " +
                        "left join fetch pd.situacao " +
                        "order by pd.id desc",
                ProfessorDisciplinaEntity.class
        ).getResultList();
    }

    public List<ProfessorDisciplinaEntity> findRecent(int limit) {
        return em.createQuery(
                        "select pd from ProfessorDisciplinaEntity pd " +
                                "left join fetch pd.professor " +
                                "left join fetch pd.disciplina " +
                                "left join fetch pd.situacao " +
                                "order by pd.id asc",
                        ProfessorDisciplinaEntity.class
                )
                .setMaxResults(Math.max(1, limit))
                .getResultList();
    }

    public List<ProfessorDisciplinaEntity> findByFilters(Long professorId, Long disciplinaId, Long situacaoId) {
        StringBuilder jpql = new StringBuilder(
                "select pd from ProfessorDisciplinaEntity pd " +
                        "left join fetch pd.professor " +
                        "left join fetch pd.disciplina " +
                        "left join fetch pd.situacao " +
                        "where 1=1"
        );
        if (professorId != null) {
            jpql.append(" and pd.professor.id = :professorId");
        }
        if (disciplinaId != null) {
            jpql.append(" and pd.disciplina.id = :disciplinaId");
        }
        if (situacaoId != null) {
            jpql.append(" and pd.situacao.id = :situacaoId");
        }
        jpql.append(" order by pd.id desc");

        TypedQuery<ProfessorDisciplinaEntity> query = em.createQuery(jpql.toString(), ProfessorDisciplinaEntity.class);
        if (professorId != null) {
            query.setParameter("professorId", professorId);
        }
        if (disciplinaId != null) {
            query.setParameter("disciplinaId", disciplinaId);
        }
        if (situacaoId != null) {
            query.setParameter("situacaoId", situacaoId);
        }
        return query.getResultList();
    }

    public boolean existsByProfessorAndDisciplina(Long professorId, Long disciplinaId) {
        Long total = em.createQuery(
                        "select count(at) from ProfessorDisciplinaEntity at " +
                                "where at.professor.id = :professorId and at.disciplina.id = :disciplinaId",
                        Long.class
                )
                .setParameter("professorId", professorId)
                .setParameter("disciplinaId", disciplinaId)
                .getSingleResult();
        return total != null && total > 0;
    }

    public boolean existsByProfessorAndDisciplinaExcludingId(Long professorId, Long disciplinaId, Long id) {
        Long total = em.createQuery(
                        "select count(at) from ProfessorDisciplinaEntity at " +
                                "where at.professor.id = :professorId and at.disciplina.id = :disciplinaId and at.id <> :id",
                        Long.class
                )
                .setParameter("professorId", professorId)
                .setParameter("disciplinaId", disciplinaId)
                .setParameter("id", id == null ? -1L : id)
                .getSingleResult();
        return total != null && total > 0;
    }
}
