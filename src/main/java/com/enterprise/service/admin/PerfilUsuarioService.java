package com.enterprise.service.admin;

import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.repository.admin.PerfilUsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PerfilUsuarioService {

    @Inject
    private PerfilUsuarioRepository repository;

    public List<PerfilUsuarioEntity> listar() {
        return repository.findAll();
    }
}
