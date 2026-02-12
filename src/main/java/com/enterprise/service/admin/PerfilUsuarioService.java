package com.enterprise.service.admin;

import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.repository.admin.PerfilUsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PerfilUsuarioService {

    @Inject
    private PerfilUsuarioRepository repository;

    public List<PerfilUsuarioDTO> listarDTO() {
        return repository.findAll()
                .stream()
                .map(PerfilUsuarioDTO::new)
                .toList();
    }

    public List<PerfilUsuarioDTO> listarPorFiltros(String login, Long perfilId, Long situacaoId) {
        return repository.findByFilters(login, perfilId, situacaoId)
                .stream()
                .map(PerfilUsuarioDTO::new)
                .toList();
    }

    @Transactional
    public PerfilUsuarioDTO vincular(PerfilUsuarioDTO dto) {
        validar(dto);

        PerfilUsuarioEntity entity = new PerfilUsuarioEntity(dto);
        repository.vincular(entity);
        return new PerfilUsuarioDTO(entity);
    }

    private void validar(PerfilUsuarioDTO dto) {
        if (dto.getUsuario() == null || dto.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório.");
        }
        if (dto.getPerfil() == null || dto.getPerfil().getId() == null) {
            throw new IllegalArgumentException("Perfil é obrigatório.");
        }
    }
}
