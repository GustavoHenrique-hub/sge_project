package com.enterprise.repository.admin;

import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
        return em.createQuery("select pu from PerfilUsuarioEntity pu order by pu.id", PerfilUsuarioEntity.class)
                .getResultList();
    }
}
