package com.enterprise.service.admin;

import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.repository.admin.PerfilUsuarioRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class PerfilUsuarioService {

    @Inject
    private PerfilUsuarioRepository repository;
    @Inject
    private SituacaoService situacaoService;

    public List<PerfilUsuarioDTO> listarDTO() {
        return repository.findAll().stream().map(PerfilUsuarioDTO::new).toList();
    }

    public List<PerfilUsuarioDTO> listarPorFiltros(String login, Long perfilId, Long situacaoId) {
        return repository.findByFilters(login, perfilId, situacaoId).stream().map(PerfilUsuarioDTO::new).toList();
    }

    public Optional<PerfilUsuarioEntity> autenticar(String login, String senha) {
        return repository.findAtivoByLoginAndSenha(login, senha);
    }

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

    public PerfilUsuarioDTO ativarVinculo(Long id) {
        SituacaoEntity situacao = situacaoService.findBySituacao("ATIVO").orElseThrow(() -> new IllegalStateException("Situação ATIVO não encontrada."));
        PerfilUsuarioEntity entity = repository.atualizarSituacao(id, situacao);
        return new PerfilUsuarioDTO(entity);
    }

    public PerfilUsuarioDTO inativarVinculo(Long id) {
        SituacaoEntity situacao = situacaoService.findBySituacao("INATIVO").orElseThrow(() -> new IllegalStateException("Situação INATIVO não encontrada."));
        PerfilUsuarioEntity entity = repository.atualizarSituacao(id, situacao);
        return new PerfilUsuarioDTO(entity);
    }

    public void excluirVinculoSeInativo(Long id) {
        PerfilUsuarioEntity entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Vínculo não encontrado."));
        String situacao = entity.getSituacao() == null ? null : entity.getSituacao().getSituacao();
        if (situacao == null || !situacao.equalsIgnoreCase("INATIVO")) {
            throw new IllegalStateException("Só é permitido excluir vínculos inativos.");
        }
        repository.excluir(entity);
    }

}
