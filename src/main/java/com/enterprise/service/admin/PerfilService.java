package com.enterprise.service.admin;

import com.enterprise.model.entity.admin.PerfilEntity;
import com.enterprise.repository.admin.PerfilRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de PerfilService.
 */

@RequestScoped
public class PerfilService {

    @Inject
    private PerfilRepository repository;
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<PerfilEntity> findAll() {
        return repository.findAll();
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<PerfilEntity> findById(Long id){
        return repository.findById(id);
    }
}
