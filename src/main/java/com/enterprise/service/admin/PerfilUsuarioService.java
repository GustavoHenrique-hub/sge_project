package com.enterprise.service.admin;

import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.repository.admin.PerfilUsuarioRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de PerfilUsuarioService.
 */

@RequestScoped
public class PerfilUsuarioService {

    @Inject
    private PerfilUsuarioRepository repository;
    @Inject
    private SituacaoService situacaoService;
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public List<PerfilUsuarioDTO> listarDTO() {
        return repository.findAll().stream().map(PerfilUsuarioDTO::new).toList();
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public List<PerfilUsuarioDTO> listarPorFiltros(Long profissionalId, Long perfilId, Long situacaoId) {
        return repository.findByFilters(profissionalId, perfilId, situacaoId).stream().map(PerfilUsuarioDTO::new).toList();
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public Optional<PerfilUsuarioEntity> autenticar(String login, String senha) {
        return repository.findAtivoByLoginAndSenha(login, senha);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public PerfilUsuarioDTO vincular(PerfilUsuarioDTO dto) {
        validar(dto);

        PerfilUsuarioEntity entity = new PerfilUsuarioEntity(dto);
        repository.vincular(entity);
        return new PerfilUsuarioDTO(entity);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private void validar(PerfilUsuarioDTO dto) {
        if (dto.getUsuario() == null || dto.getUsuario().getId() == null) {
            throw new IllegalArgumentException("UsuÃƒÂ¡rio ÃƒÂ© obrigatÃƒÂ³rio.");
        }
        if (dto.getPerfil() == null || dto.getPerfil().getId() == null) {
            throw new IllegalArgumentException("Perfil ÃƒÂ© obrigatÃƒÂ³rio.");
        }
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public PerfilUsuarioDTO ativarVinculo(Long id) {
        SituacaoEntity situacao = situacaoService.findBySituacao("ATIVO").orElseThrow(() -> new IllegalStateException("SituaÃƒÂ§ÃƒÂ£o ATIVO nÃƒÂ£o encontrada."));
        PerfilUsuarioEntity entity = repository.atualizarSituacao(id, situacao);
        return new PerfilUsuarioDTO(entity);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public PerfilUsuarioDTO inativarVinculo(Long id) {
        SituacaoEntity situacao = situacaoService.findBySituacao("INATIVO").orElseThrow(() -> new IllegalStateException("SituaÃƒÂ§ÃƒÂ£o INATIVO nÃƒÂ£o encontrada."));
        PerfilUsuarioEntity entity = repository.atualizarSituacao(id, situacao);
        return new PerfilUsuarioDTO(entity);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public void excluirVinculoSeInativo(Long id) {
        PerfilUsuarioEntity entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("VÃƒÂ­nculo nÃƒÂ£o encontrado."));
        String situacao = entity.getSituacao() == null ? null : entity.getSituacao().getSituacao();
        if (situacao == null || !situacao.equalsIgnoreCase("INATIVO")) {
            throw new IllegalStateException("SÃƒÂ³ ÃƒÂ© permitido excluir vÃƒÂ­nculos inativos.");
        }
        repository.excluir(entity);
    }

}
