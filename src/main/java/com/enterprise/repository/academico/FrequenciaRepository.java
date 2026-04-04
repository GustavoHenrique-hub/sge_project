package com.enterprise.repository.academico;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.academico.BoletimEntity;
import com.enterprise.model.entity.academico.FrequenciaEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class FrequenciaRepository {

    @Inject
    private EntityManager em;

    public FrequenciaEntity save(FrequenciaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            em.persist(entity);
            return entity;
        });
    }

    public FrequenciaEntity update(FrequenciaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            return em.merge(entity);
        });
    }

    public Optional<FrequenciaEntity> findById(Long id) {
        return Optional.ofNullable(em.find(FrequenciaEntity.class, id));
    }

    public Optional<FrequenciaEntity> findByBoletimAndDisciplina(Long boletimId, Long disciplinaId) {
        return em.createQuery(
                        "select f from FrequenciaEntity f " +
                                "left join fetch f.boletim b " +
                                "left join fetch b.aluno " +
                                "left join fetch b.turma " +
                                "left join fetch f.disciplina " +
                                "where b.id = :boletimId and f.disciplina.id = :disciplinaId " +
                                "order by f.id desc",
                        FrequenciaEntity.class
                )
                .setParameter("boletimId", boletimId)
                .setParameter("disciplinaId", disciplinaId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public List<FrequenciaEntity> findByBoletim(Long boletimId) {
        return em.createQuery(
                        "select f from FrequenciaEntity f " +
                                "left join fetch f.disciplina " +
                                "where f.boletim.id = :boletimId " +
                                "order by f.disciplina.descricao, f.id desc",
                        FrequenciaEntity.class
                )
                .setParameter("boletimId", boletimId)
                .getResultList();
    }

    private void attachReferences(FrequenciaEntity entity) {
        if (entity.getBoletim() != null && entity.getBoletim().getId() != null) {
            entity.setBoletim(em.getReference(BoletimEntity.class, entity.getBoletim().getId()));
        }
        if (entity.getDisciplina() != null && entity.getDisciplina().getId() != null) {
            entity.setDisciplina(em.getReference(DisciplinaEntity.class, entity.getDisciplina().getId()));
        }
    }
}
