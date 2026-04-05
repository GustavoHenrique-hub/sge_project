package com.enterprise.repository.gestao;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.gestao.AlunoEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a AlunoRepository.
 */

@RequestScoped
public class AlunoRepository {

    @Inject
    private EntityManager em;
    /**
     * Persiste um novo registro no banco dentro do controle transacional da aplicacao.
     */

    public AlunoEntity save(AlunoEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }
    /**
     * Mescla e salva as alteracoes de um registro ja existente no banco de dados.
     */

    public AlunoEntity update(AlunoEntity entity) {
        return JpaTransaction.execute(em, () -> em.merge(entity));
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<AlunoEntity> findAll() {
        return em.createQuery("select p from AlunoEntity p order by p.nome", AlunoEntity.class)
                .getResultList();
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public List<AlunoEntity> findByFilters(String nome, String cpf) {
        StringBuilder jpql = new StringBuilder("select a from AlunoEntity a where 1=1");
        if (nome != null && !nome.isBlank()) {
            jpql.append(" and upper(a.nome) like :nome");
        }
        if (cpf != null && !cpf.isBlank()) {
            jpql.append(" and replace(replace(replace(a.cpf, '.', ''), '-', ''), '/', '') like :cpf");
        }
        jpql.append(" order by a.nome");

        TypedQuery<AlunoEntity> query = em.createQuery(jpql.toString(), AlunoEntity.class);
        if (nome != null && !nome.isBlank()) {
            query.setParameter("nome", "%" + nome.trim().toUpperCase() + "%");
        }
        if (cpf != null && !cpf.isBlank()) {
            query.setParameter("cpf", "%" + normalizeCpf(cpf) + "%");
        }
        return query.getResultList();
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<AlunoEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select a from AlunoEntity a where a.id = :id",
                        AlunoEntity.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
    /**
     * Remove variacoes de formato para padronizar a comparacao ou o armazenamento do valor.
     */

    private String normalizeCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("[^0-9]", "");
    }
}
