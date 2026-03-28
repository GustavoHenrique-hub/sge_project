package com.enterprise.service.admin;

import com.enterprise.dto.admin.UsuarioDTO;
import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.repository.admin.UsuarioRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class UsuarioService {

    @Inject
    private UsuarioRepository repository;

    public UsuarioDTO criar(UsuarioDTO dto) {
        validar(dto);

        UsuarioEntity entity = new UsuarioEntity(dto);
        repository.save(entity);
        return new UsuarioDTO(entity);
    }

    public UsuarioDTO atualizar(UsuarioDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID é obrigatório para atualizar.");
        }
        validar(dto);

        UsuarioEntity entity = new UsuarioEntity(dto);
        UsuarioEntity merged = repository.update(entity);
        return new UsuarioDTO(merged);
    }

    public List<UsuarioEntity> listar() {
        return repository.findAll();
    }

    public Optional<UsuarioEntity> findById(Long id) {
        return repository.findById(id);
    }

    public void remover(Long id) {
        repository.removeById(id);
    }

    private void validar(UsuarioDTO dto) {
        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            throw new IllegalArgumentException("Login é obrigatório.");
        }
        if (dto.getUsuario() == null || dto.getUsuario().isBlank()) {
            throw new IllegalArgumentException("Usuário é obrigatório.");
        }
    }
}
