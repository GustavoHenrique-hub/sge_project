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

@RequestScoped
public class ProfissionalService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static DocumentsValidation validator = new DocumentsValidation();

    @Inject
    private ProfissionalRepository repository;

    public ProfissionalDTO criar(ProfissionalDTO dto) {
        validar(dto);
        ProfissionalEntity entity = new ProfissionalEntity(dto);
        repository.save(entity);
        return new ProfissionalDTO(entity);
    }

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

    public List<ProfissionalEntity> findAll() {
        return repository.findAll();
    }

    public List<ProfissionalEntity> findByFilters(String nome, String cpf) {
        return repository.findByFilters(nome, cpf);
    }

    public List<ProfissionalEntity> findProfessoresAtivosByTermo(String termo) {
        return repository.findProfessoresAtivosByTermo(termo);
    }

    public Optional<ProfissionalEntity> findById(Long id){
        return repository.findById(id);
    }

    private void validar(ProfissionalDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Profissional invalido.");
        }

        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome Ã© obrigatÃ³rio.");
        }
        boolean validacaoCPF = validator.cpfValidation(dto.getCpf());
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF Ã© obrigatÃ³rio.");
        }else {
            if (validacaoCPF != true) {
                throw new IllegalArgumentException("CPF Ã© invÃ¡lido!");
            }
        }

        boolean validacaoRG = validator.rgValidation(dto.getRg());
        if (dto.getRg() == null || dto.getRg().isBlank()) {
            throw new IllegalArgumentException("RG Ã© obrigatÃ³rio.");
        }else{
            if (validacaoRG != true) {
                throw new IllegalArgumentException("RG Ã© invÃ¡lido!");
            }
        }
        if (dto.getDtNasc() == null) {
            throw new IllegalArgumentException("Data de nascimento Ã© obrigatÃ³ria.");
        }
        if (dto.getDtNasc().after(new Date())) {
            throw new IllegalArgumentException("Data de nascimento invÃ¡lida.");
        }
        if (dto.getRm() == null){
            dto.setRm(String.format("%05d", RANDOM.nextInt(100000)));
        }
    }

    private String normalizarRg(String valor) {
        return valor == null ? null : valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
    }
}

