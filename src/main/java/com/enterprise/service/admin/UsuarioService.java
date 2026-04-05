package com.enterprise.service.admin;

import com.enterprise.dto.admin.UsuarioDTO;
import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import com.enterprise.repository.admin.UsuarioRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de UsuarioService.
 */

@RequestScoped
public class UsuarioService {

    @Inject
    private UsuarioRepository repository;
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public UsuarioDTO criar(UsuarioDTO dto) {
        validar(dto);

        Long profissionalId = dto.getProfissional().getId();
        if (repository.findByProfissionalId(profissionalId).isPresent()) {
            throw new IllegalArgumentException("Ja existe um usuario vinculado a este profissional.");
        }

        UsuarioEntity entity = new UsuarioEntity(dto);
        repository.save(entity);
        return new UsuarioDTO(entity);
    }
    /**
     * Atualiza o registro existente aplicando as regras de negocio desta camada.
     */

    public UsuarioDTO atualizar(UsuarioDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID e obrigatorio para atualizar.");
        }
        validar(dto);

        Long profissionalId = dto.getProfissional().getId();
        Optional<UsuarioEntity> existenteProfissional = repository.findByProfissionalId(profissionalId);
        if (existenteProfissional.isPresent() && !existenteProfissional.get().getId().equals(dto.getId())) {
            throw new IllegalArgumentException("Ja existe um usuario vinculado a este profissional.");
        }

        UsuarioEntity entity = new UsuarioEntity(dto);
        UsuarioEntity merged = repository.update(entity);
        return new UsuarioDTO(merged);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public List<UsuarioEntity> listar() {
        return repository.findAll();
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<UsuarioEntity> findById(Long id) {
        return repository.findById(id);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public void remover(Long id) {
        repository.removeById(id);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public UsuarioEntity login(String login, String senha) {
        return repository.findByLoginAndSenha(login, senha).orElse(null);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private void validar(UsuarioDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Usuario invalido.");
        }
        if (dto.getProfissional() == null || dto.getProfissional().getId() == null) {
            throw new IllegalArgumentException("Profissional e obrigatorio.");
        }
        if (dto.getProfissional().getCpf() == null || dto.getProfissional().getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF do profissional e obrigatorio para gerar o login.");
        }
        if (dto.getSessionTimeout() == null) {
            throw new IllegalArgumentException("Session timeout e obrigatorio.");
        }
        if (dto.getSessionTimeout() <= 0) {
            throw new IllegalArgumentException("Session timeout deve ser um numero inteiro maior que zero.");
        }

        String cpfSemPontuacao = dto.getProfissional().getCpf().replaceAll("\\D", "");
        if (cpfSemPontuacao.isBlank()) {
            throw new IllegalArgumentException("CPF do profissional invalido para gerar o login.");
        }

        dto.setLogin(cpfSemPontuacao);
        dto.setSenha(cpfSemPontuacao + "_@ABC");
    }
}
