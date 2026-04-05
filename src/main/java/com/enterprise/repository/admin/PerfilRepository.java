package com.enterprise.repository.admin;

import com.enterprise.model.entity.admin.PerfilEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a PerfilRepository.
 */

@RequestScoped
public class PerfilRepository {

    @Inject
    private EntityManager em;
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<PerfilEntity> findAll() {
        return em.createQuery("select p from PerfilEntity p order by p.id", PerfilEntity.class)
                .getResultList();
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<PerfilEntity> findById(Long id) {
        return Optional.ofNullable(em.find(PerfilEntity.class, id));
    }
}
