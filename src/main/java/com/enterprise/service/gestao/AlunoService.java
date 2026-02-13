package com.enterprise.service.gestao;

import com.enterprise.model.entity.admin.PerfilEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.repository.admin.PerfilRepository;
import com.enterprise.repository.gestao.AlunoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AlunoService {

    @Inject
    private AlunoRepository repository;

    public List<AlunoEntity> findAll() {
        return repository.findAll();
    }

    public Optional<AlunoEntity> findById(Long id){
        return repository.findById(id);
    }
}
