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

    public List<PerfilUsuarioDTO> listarPorFiltros(Long profissionalId, Long perfilId, Long situacaoId) {
        return repository.findByFilters(profissionalId, perfilId, situacaoId).stream().map(PerfilUsuarioDTO::new).toList();
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
            throw new IllegalArgumentException("UsuÃ¡rio Ã© obrigatÃ³rio.");
        }
        if (dto.getPerfil() == null || dto.getPerfil().getId() == null) {
            throw new IllegalArgumentException("Perfil Ã© obrigatÃ³rio.");
        }
    }

    public PerfilUsuarioDTO ativarVinculo(Long id) {
        SituacaoEntity situacao = situacaoService.findBySituacao("ATIVO").orElseThrow(() -> new IllegalStateException("SituaÃ§Ã£o ATIVO nÃ£o encontrada."));
        PerfilUsuarioEntity entity = repository.atualizarSituacao(id, situacao);
        return new PerfilUsuarioDTO(entity);
    }

    public PerfilUsuarioDTO inativarVinculo(Long id) {
        SituacaoEntity situacao = situacaoService.findBySituacao("INATIVO").orElseThrow(() -> new IllegalStateException("SituaÃ§Ã£o INATIVO nÃ£o encontrada."));
        PerfilUsuarioEntity entity = repository.atualizarSituacao(id, situacao);
        return new PerfilUsuarioDTO(entity);
    }

    public void excluirVinculoSeInativo(Long id) {
        PerfilUsuarioEntity entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("VÃ­nculo nÃ£o encontrado."));
        String situacao = entity.getSituacao() == null ? null : entity.getSituacao().getSituacao();
        if (situacao == null || !situacao.equalsIgnoreCase("INATIVO")) {
            throw new IllegalStateException("SÃ³ Ã© permitido excluir vÃ­nculos inativos.");
        }
        repository.excluir(entity);
    }

}
