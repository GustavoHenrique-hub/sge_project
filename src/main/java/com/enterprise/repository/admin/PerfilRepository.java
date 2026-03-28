package com.enterprise.repository.admin;

import com.enterprise.model.entity.admin.PerfilEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PerfilRepository {

    @Inject
    private EntityManager em;

    public List<PerfilEntity> findAll() {
        return em.createQuery("select p from PerfilEntity p order by p.id", PerfilEntity.class)
                .getResultList();
    }

    public Optional<PerfilEntity> findById(Long id) {
        return Optional.ofNullable(em.find(PerfilEntity.class, id));
    }
}
