package com.enterprise.repository.admin;

import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PerfilUsuarioRepository {

    @PersistenceContext
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

    public List<PerfilUsuarioEntity> findByFilters(String login, Long perfilId){
        StringBuilder jpql = new StringBuilder(
                "select pu from PerfilUsuarioEntity pu " +
                        "left join fetch pu.perfil " +
                        "left join fetch pu.usuario " +
                        "where 1=1"
        );

        if (login != null && !login.isBlank()) {
            jpql.append(" and lower(pu.usuario.login) like :login");
        }
        if (perfilId != null) {
            jpql.append(" and pu.perfil.id = :perfilId");
        }

        TypedQuery<PerfilUsuarioEntity> query = em.createQuery(jpql.toString(), PerfilUsuarioEntity.class);
        if (login != null && !login.isBlank()) {
            query.setParameter("login", "%" + login.toLowerCase() + "%");
        }
        if (perfilId != null) {
            query.setParameter("perfilId", perfilId);
        }
        return query.getResultList();
    }

    public PerfilUsuarioEntity vincular (PerfilUsuarioEntity entity){
        em.persist(entity);
        return entity;
    }
}
