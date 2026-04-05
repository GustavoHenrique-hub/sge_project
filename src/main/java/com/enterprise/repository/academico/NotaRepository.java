package com.enterprise.repository.academico;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.academico.BoletimEntity;
import com.enterprise.model.entity.academico.NotaEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a NotaRepository.
 */

@RequestScoped
public class NotaRepository {

    @Inject
    private EntityManager em;
    /**
     * Persiste um novo registro no banco dentro do controle transacional da aplicacao.
     */

    public NotaEntity save(NotaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            em.persist(entity);
            return entity;
        });
    }
    /**
     * Mescla e salva as alteracoes de um registro ja existente no banco de dados.
     */

    public NotaEntity update(NotaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            return em.merge(entity);
        });
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<NotaEntity> findById(Long id) {
        return Optional.ofNullable(em.find(NotaEntity.class, id));
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public Optional<NotaEntity> findByBoletimAndDisciplina(Long boletimId, Long disciplinaId) {
        return em.createQuery(
                        "select n from NotaEntity n " +
                                "left join fetch n.boletim b " +
                                "left join fetch b.aluno " +
                                "left join fetch b.turma " +
                                "left join fetch n.disciplina " +
                                "where b.id = :boletimId and n.disciplina.id = :disciplinaId " +
                                "order by n.id desc",
                        NotaEntity.class
                )
                .setParameter("boletimId", boletimId)
                .setParameter("disciplinaId", disciplinaId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public List<NotaEntity> findByBoletim(Long boletimId) {
        return em.createQuery(
                        "select n from NotaEntity n " +
                                "left join fetch n.disciplina " +
                                "where n.boletim.id = :boletimId " +
                                "order by n.disciplina.descricao, n.id desc",
                        NotaEntity.class
                )
                .setParameter("boletimId", boletimId)
                .getResultList();
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    private void attachReferences(NotaEntity entity) {
        if (entity.getBoletim() != null && entity.getBoletim().getId() != null) {
            entity.setBoletim(em.getReference(BoletimEntity.class, entity.getBoletim().getId()));
        }
        if (entity.getDisciplina() != null && entity.getDisciplina().getId() != null) {
            entity.setDisciplina(em.getReference(DisciplinaEntity.class, entity.getDisciplina().getId()));
        }
    }
}
