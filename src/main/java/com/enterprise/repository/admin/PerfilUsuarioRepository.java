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

@RequestScoped
public class PerfilUsuarioRepository {

    @Inject
    private EntityManager em;

    public Optional<PerfilUsuarioEntity> findById(Long id) {
        return Optional.ofNullable(em.find(PerfilUsuarioEntity.class, id));
    }

    public List<PerfilUsuarioEntity> findAll() {
        return em.createQuery(
                "select pu from PerfilUsuarioEntity pu " +
                        "left join fetch pu.perfil " +
                        "left join fetch pu.usuario",
                PerfilUsuarioEntity.class
        ).getResultList();
    }

    public List<PerfilUsuarioEntity> findByFilters(String login, Long perfilId, Long situacaoId) {
        StringBuilder jpql = new StringBuilder(
                "select pu from PerfilUsuarioEntity pu " +
                        "left join fetch pu.perfil " +
                        "left join fetch pu.usuario " +
                        "left join fetch pu.situacao " +
                        "where 1=1"
        );

        if (login != null && !login.isBlank()) {
            jpql.append(" and lower(pu.usuario.login) like :login");
        }
        if (perfilId != null) {
            jpql.append(" and pu.perfil.id = :perfilId");
        }
        if (situacaoId != null) {
            jpql.append(" and pu.situacao.id = :situacaoId");
        }

        TypedQuery<PerfilUsuarioEntity> query = em.createQuery(jpql.toString(), PerfilUsuarioEntity.class);
        if (login != null && !login.isBlank()) {
            query.setParameter("login", "%" + login.toLowerCase() + "%");
        }
        if (perfilId != null) {
            query.setParameter("perfilId", perfilId);
        }
        if (situacaoId != null) {
            query.setParameter("situacaoId", situacaoId);
        }
        return query.getResultList();
    }

    public PerfilUsuarioEntity vincular(PerfilUsuarioEntity entity) {
        return JpaTransaction.execute(em, () -> {
            attachReferences(entity);
            em.persist(entity);
            return entity;
        });
    }

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

    public void excluir(PerfilUsuarioEntity entity) {
        JpaTransaction.run(em, () -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }

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
