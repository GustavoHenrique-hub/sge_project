package com.enterprise.repository.admin;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.admin.PerfilEntity;
import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.admin.UsuarioEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a PerfilUsuarioRepository.
 */

@RequestScoped
public class PerfilUsuarioRepository {

    @Inject
    private EntityManager em;
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<PerfilUsuarioEntity> findById(Long id) {
        return Optional.ofNullable(em.find(PerfilUsuarioEntity.class, id));
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<PerfilUsuarioEntity> findAll() {
        return em.createQuery(
                "select pu from PerfilUsuarioEntity pu " +
                        "left join fetch pu.perfil " +
                        "left join fetch pu.usuario u " +
                        "left join fetch u.profissional",
                PerfilUsuarioEntity.class
        ).getResultList();
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public List<PerfilUsuarioEntity> findByFilters(Long profissionalId, Long perfilId, Long situacaoId) {
        StringBuilder jpql = new StringBuilder(
                "select pu from PerfilUsuarioEntity pu " +
                        "left join fetch pu.perfil " +
                        "left join fetch pu.usuario u " +
                        "left join fetch u.profissional " +
                        "left join fetch pu.situacao " +
                        "where 1=1"
        );

        if (profissionalId != null) {
            jpql.append(" and u.profissional.id = :profissionalId");
        }
        if (perfilId != null) {
            jpql.append(" and pu.perfil.id = :perfilId");
        }
        if (situacaoId != null) {
            jpql.append(" and pu.situacao.id = :situacaoId");
        }

        TypedQuery<PerfilUsuarioEntity> query = em.createQuery(jpql.toString(), PerfilUsuarioEntity.class);
        if (profissionalId != null) {
            query.setParameter("profissionalId", profissionalId);
        }
        if (perfilId != null) {
            query.setParameter("perfilId", perfilId);
        }
        if (situacaoId != null) {
            query.setParameter("situacaoId", situacaoId);
        }
        return query.getResultList();
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public Optional<PerfilUsuarioEntity> findAtivoByLoginAndSenha(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }

        return em.createQuery(
                        "select pu from PerfilUsuarioEntity pu " +
                                "join fetch pu.usuario u " +
                                "join fetch u.profissional pr " +
                                "join fetch pu.perfil p " +
                                "join fetch pu.situacao s " +
                                "where u.login = :login " +
                                "and u.senha = :senha " +
                                "and upper(s.situacao) = :situacao " +
                                "order by pu.id desc",
                        PerfilUsuarioEntity.class
                )
                .setParameter("login", login.replaceAll("\\D", ""))
                .setParameter("senha", senha)
                .setParameter("situacao", "ATIVO")
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public PerfilUsuarioEntity vincular(PerfilUsuarioEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            em.persist(entity);
            return entity;
        });
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public PerfilUsuarioEntity atualizarSituacao(Long id, SituacaoEntity situacao) {
        return JpaTransaction.execute(em, () -> {
            PerfilUsuarioEntity entity = em.find(PerfilUsuarioEntity.class, id);
            if (entity == null) {
                throw new IllegalArgumentException("Vinculo nao encontrado.");
            }
            entity.setSituacao(em.getReference(SituacaoEntity.class, situacao.getId()));
            return entity;
        });
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public void excluir(PerfilUsuarioEntity entity) {
        JpaTransaction.run(em, () -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    private void attachReferences(PerfilUsuarioEntity entity) {
        if (entity.getPerfil() != null && entity.getPerfil().getId() != null) {
            entity.setPerfil(em.getReference(PerfilEntity.class, entity.getPerfil().getId()));
        }
        if (entity.getUsuario() != null && entity.getUsuario().getId() != null) {
            entity.setUsuario(em.getReference(UsuarioEntity.class, entity.getUsuario().getId()));
        }
        if (entity.getSituacao() != null && entity.getSituacao().getId() != null) {
            entity.setSituacao(em.getReference(SituacaoEntity.class, entity.getSituacao().getId()));
        }
    }
}
