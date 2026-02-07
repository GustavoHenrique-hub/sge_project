package com.enterprise.repository.admin;

import com.enterprise.model.entity.admin.UsuarioEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository {

    @PersistenceContext
    private EntityManager em;

    public UsuarioEntity save(UsuarioEntity entity) {
        em.persist(entity);
        return entity;
    }

    public UsuarioEntity update(UsuarioEntity entity) {
        return em.merge(entity);
    }

    public Optional<UsuarioEntity> findById(Long id) {
        return Optional.ofNullable(em.find(UsuarioEntity.class, id));
    }

    public List<UsuarioEntity> findAll() {
        return em.createQuery("select u from UsuarioEntity u order by u.id desc", UsuarioEntity.class)
                .getResultList();
    }

    public void removeById(Long id) {
        UsuarioEntity ref = em.find(UsuarioEntity.class, id);
        if (ref != null) {
            em.remove(ref);
        }
    }
}