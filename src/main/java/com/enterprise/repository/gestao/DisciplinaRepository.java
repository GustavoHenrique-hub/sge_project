package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a DisciplinaRepository.
 */

@RequestScoped
public class DisciplinaRepository {

    @Inject
    private EntityManager em;
    /**
     * Persiste um novo registro no banco dentro do controle transacional da aplicacao.
     */

    public DisciplinaEntity save(DisciplinaEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }
    /**
     * Mescla e salva as alteracoes de um registro ja existente no banco de dados.
     */

    public DisciplinaEntity update(DisciplinaEntity entity) {
        return JpaTransaction.execute(em, () -> em.merge(entity));
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<DisciplinaEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select d from DisciplinaEntity d where d.id = :id",
                        DisciplinaEntity.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<DisciplinaEntity> findAll() {
        return em.createQuery("select d from DisciplinaEntity d order by d.descricao", DisciplinaEntity.class)
                .getResultList();
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public void remove(DisciplinaEntity entity) {
        JpaTransaction.run(em, () -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }
}
