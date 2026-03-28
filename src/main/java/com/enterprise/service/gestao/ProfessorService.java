package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.ProfessorDTO;
import com.enterprise.model.entity.gestao.ProfessorEntity;
import com.enterprise.repository.gestao.ProfessorRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.sql.Array;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.enterprise.validation.DocumentsValidation;

@RequestScoped
public class ProfessorService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static DocumentsValidation validator = new DocumentsValidation();

    @Inject
    private ProfessorRepository repository;

    public ProfessorDTO criar(ProfessorDTO dto) {
        validar(dto);
        ProfessorEntity entity = new ProfessorEntity(dto);
        repository.save(entity);
        return new ProfessorDTO(entity);
    }

    public ProfessorDTO atualizar(ProfessorDTO dto) {
        validar(dto);
        if (dto.getId() == null || dto.getRm() == null || dto.getRm().isBlank()) {
            throw new IllegalArgumentException("Professor invalido para atualizacao.");
        }
        ProfessorEntity existente = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Professor nao encontrado."));
        existente.setNome(dto.getNome());
        existente.setCpf(dto.getCpf());
        existente.setRg(dto.getRg());
        existente.setDtNasc(dto.getDtNasc());
        existente.setEmail(dto.getEmail());
        existente.setTelefone(dto.getTelefone());
        ProfessorEntity merged = repository.update(existente);
        return new ProfessorDTO(merged);
    }

    public List<ProfessorEntity> findAll() {
        return repository.findAll();
    }

    public List<ProfessorEntity> findByFilters(String nome, String cpf) {
        return repository.findByFilters(nome, cpf);
    }

    public Optional<ProfessorEntity> findById(Long id){
        return repository.findById(id);
    }

    private void validar(ProfessorDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Professor invalido.");
        }

        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        boolean validacaoCPF = validator.cpfValidation(dto.getCpf());
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }else {
            if (validacaoCPF != true) {
                throw new IllegalArgumentException("CPF é inválido!");
            }
        }

        boolean validacaoRG = validator.rgValidation(dto.getRg());
        if (dto.getRg() == null || dto.getRg().isBlank()) {
            throw new IllegalArgumentException("RG é obrigatório.");
        }else{
            if (validacaoRG != true) {
                throw new IllegalArgumentException("RG é inválido!");
            }
        }
        if (dto.getDtNasc() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória.");
        }
        if (dto.getDtNasc().after(new Date())) {
            throw new IllegalArgumentException("Data de nascimento inválida.");
        }
        if (dto.getRm() == null){
            dto.setRm(String.format("%05d", RANDOM.nextInt(100000)));
        }
    }

    private String normalizarRg(String valor) {
        return valor == null ? null : valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
    }
}
