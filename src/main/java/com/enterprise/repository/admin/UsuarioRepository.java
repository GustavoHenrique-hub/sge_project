package com.enterprise.repository.admin;

import com.enterprise.config.JpaTransaction;
import com.enterprise.model.entity.admin.UsuarioEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class UsuarioRepository {

    @Inject
    private EntityManager em;

    public UsuarioEntity save(UsuarioEntity entity) {
        return JpaTransaction.execute(em, () -> {
            em.persist(entity);
            return entity;
        });
    }

    public UsuarioEntity update(UsuarioEntity entity) {
        return JpaTransaction.execute(em, () -> em.merge(entity));
    }

    public Optional<UsuarioEntity> findById(Long id) {
        return Optional.ofNullable(em.find(UsuarioEntity.class, id));
    }

    public List<UsuarioEntity> findAll() {
        return em.createQuery("select u from UsuarioEntity u order by u.id desc", UsuarioEntity.class)
                .getResultList();
    }

    public void removeById(Long id) {
        JpaTransaction.run(em, () -> {
            UsuarioEntity ref = em.find(UsuarioEntity.class, id);
            if (ref != null) {
                em.remove(ref);
            }
        });
    }
}
