package com.enterprise.repository.admin;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a UsuarioRepository.
 */

@RequestScoped
public class UsuarioRepository {

    @Inject
    private EntityManager em;
    /**
     * Persiste um novo registro no banco dentro do controle transacional da aplicacao.
     */

    public UsuarioEntity save(UsuarioEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            em.persist(entity);
            return entity;
        });
    }
    /**
     * Mescla e salva as alteracoes de um registro ja existente no banco de dados.
     */

    public UsuarioEntity update(UsuarioEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            return em.merge(entity);
        });
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<UsuarioEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select u from UsuarioEntity u " +
                                "left join fetch u.profissional " +
                                "where u.id = :id",
                        UsuarioEntity.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<UsuarioEntity> findAll() {
        return em.createQuery(
                        "select u from UsuarioEntity u " +
                                "left join fetch u.profissional " +
                                "order by u.id desc",
                        UsuarioEntity.class
                )
                .getResultList();
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public Optional<UsuarioEntity> findByLoginAndSenha(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select u from UsuarioEntity u " +
                                "left join fetch u.profissional " +
                                "where u.login = :login and u.senha = :senha",
                        UsuarioEntity.class
                )
                .setParameter("login", login.trim().replaceAll("\\D", ""))
                .setParameter("senha", senha)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public Optional<UsuarioEntity> findByProfissionalId(Long profissionalId) {
        if (profissionalId == null) {
            return Optional.empty();
        }
        return em.createQuery(
                        "select u from UsuarioEntity u " +
                                "left join fetch u.profissional " +
                                "where u.profissional.id = :profissionalId",
                        UsuarioEntity.class
                )
                .setParameter("profissionalId", profissionalId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public void removeById(Long id) {
        JpaTransaction.run(em, () -> {
            UsuarioEntity ref = em.find(UsuarioEntity.class, id);
            if (ref != null) {
                em.remove(ref);
            }
        });
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    private void attachReferences(UsuarioEntity entity) {
        if (entity.getProfissional() != null && entity.getProfissional().getId() != null) {
            ProfissionalEntity profissional = em.find(ProfissionalEntity.class, entity.getProfissional().getId());
            if (profissional == null) {
                throw new IllegalArgumentException("Profissional nao encontrado.");
            }
            entity.setProfissional(profissional);
        }
    }
}
