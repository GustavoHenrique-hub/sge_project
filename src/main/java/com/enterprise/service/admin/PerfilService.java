package com.enterprise.service.admin;

import com.enterprise.model.entity.admin.PerfilEntity;
import com.enterprise.repository.admin.PerfilRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PerfilService {

    @Inject
    private PerfilRepository repository;

    public List<PerfilEntity> findAll() {
        return repository.findAll();
    }

    public Optional<PerfilEntity> findById(Long id){
        return repository.findById(id);
    }
}