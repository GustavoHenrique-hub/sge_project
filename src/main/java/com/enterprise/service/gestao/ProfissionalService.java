package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.ProfissionalDTO;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import com.enterprise.repository.gestao.ProfissionalRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.sql.Array;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.enterprise.validation.DocumentsValidation;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de ProfissionalService.
 */

@RequestScoped
public class ProfissionalService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static DocumentsValidation validator = new DocumentsValidation();

    @Inject
    private ProfissionalRepository repository;
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public ProfissionalDTO criar(ProfissionalDTO dto) {
        validar(dto);
        ProfissionalEntity entity = new ProfissionalEntity(dto);
        repository.save(entity);
        return new ProfissionalDTO(entity);
    }
    /**
     * Atualiza o registro existente aplicando as regras de negocio desta camada.
     */

    public ProfissionalDTO atualizar(ProfissionalDTO dto) {
        validar(dto);
        if (dto.getId() == null || dto.getRm() == null || dto.getRm().isBlank()) {
            throw new IllegalArgumentException("Profissional invalido para atualizacao.");
        }
        ProfissionalEntity existente = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Profissional nao encontrado."));
        existente.setNome(dto.getNome());
        existente.setCpf(dto.getCpf());
        existente.setRg(dto.getRg());
        existente.setDtNasc(dto.getDtNasc());
        existente.setEmail(dto.getEmail());
        existente.setTelefone(dto.getTelefone());
        ProfissionalEntity merged = repository.update(existente);
        return new ProfissionalDTO(merged);
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<ProfissionalEntity> findAll() {
        return repository.findAll();
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public List<ProfissionalEntity> findByFilters(String nome, String cpf) {
        return repository.findByFilters(nome, cpf);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public List<ProfissionalEntity> findProfessoresAtivosByTermo(String termo) {
        return repository.findProfessoresAtivosByTermo(termo);
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<ProfissionalEntity> findById(Long id){
        return repository.findById(id);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private void validar(ProfissionalDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Profissional invalido.");
        }

        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome ÃƒÂ© obrigatÃƒÂ³rio.");
        }
        boolean validacaoCPF = validator.cpfValidation(dto.getCpf());
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF ÃƒÂ© obrigatÃƒÂ³rio.");
        }else {
            if (validacaoCPF != true) {
                throw new IllegalArgumentException("CPF ÃƒÂ© invÃƒÂ¡lido!");
            }
        }

        boolean validacaoRG = validator.rgValidation(dto.getRg());
        if (dto.getRg() == null || dto.getRg().isBlank()) {
            throw new IllegalArgumentException("RG ÃƒÂ© obrigatÃƒÂ³rio.");
        }else{
            if (validacaoRG != true) {
                throw new IllegalArgumentException("RG ÃƒÂ© invÃƒÂ¡lido!");
            }
        }
        if (dto.getDtNasc() == null) {
            throw new IllegalArgumentException("Data de nascimento ÃƒÂ© obrigatÃƒÂ³ria.");
        }
        if (dto.getDtNasc().after(new Date())) {
            throw new IllegalArgumentException("Data de nascimento invÃƒÂ¡lida.");
        }
        if (dto.getRm() == null){
            dto.setRm(String.format("%05d", RANDOM.nextInt(100000)));
        }
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    private String normalizarRg(String valor) {
        return valor == null ? null : valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
    }
}

