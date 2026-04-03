package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.repository.gestao.AlunoRepository;
import com.enterprise.validation.DocumentsValidation;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RequestScoped
public class AlunoService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static DocumentsValidation validator = new DocumentsValidation();

    @Inject
    private AlunoRepository repository;

    public AlunoDTO criar(AlunoDTO dto) {
        validar(dto);
        AlunoEntity entity = new AlunoEntity(dto);
        repository.save(entity);
        return new AlunoDTO(entity);
    }

    public AlunoDTO atualizar(AlunoDTO dto) {
        validar(dto);
        if (dto.getId() == null || dto.getRm() == null || dto.getRm().isBlank()) {
            throw new IllegalArgumentException("Aluno invalido para atualizacao.");
        }
        AlunoEntity existente = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado."));
        existente.setNome(dto.getNome());
        existente.setCpf(dto.getCpf());
        existente.setRg(dto.getRg());
        existente.setDtNasc(dto.getDtNasc());
        existente.setEmail(dto.getEmail());
        existente.setTelefone(dto.getTelefone());
        AlunoEntity merged = repository.update(existente);
        return new AlunoDTO(merged);
    }

    public List<AlunoEntity> findAll() {
        return repository.findAll();
    }

    public List<AlunoEntity> findByFilters(String nome, String cpf) {
        return repository.findByFilters(nome, cpf);
    }

    public Optional<AlunoEntity> findById(Long id){
        return repository.findById(id);
    }

    private void validar(AlunoDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Aluno invalido.");
        }

        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }

        boolean validacaoCPF = validator.cpfValidation(dto.getCpf());
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF Ã© obrigatÃ³rio.");
        }else {
            if (validacaoCPF != true) {
                throw new IllegalArgumentException("CPF Ã© invÃ¡lido!");
            }
        }
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF e obrigatorio.");
        }

        boolean validacaoRG = validator.rgValidation(dto.getRg());
        if (dto.getRg() == null || dto.getRg().isBlank()) {
            throw new IllegalArgumentException("RG Ã© obrigatÃ³rio.");
        }else{
            if (validacaoRG != true) {
                throw new IllegalArgumentException("RG Ã© invÃ¡lido!");
            }
        }
        if (dto.getRg() == null || dto.getRg().isBlank()) {
            throw new IllegalArgumentException("RG e obrigatorio.");
        }
        if (dto.getDtNasc() == null) {
            throw new IllegalArgumentException("Data de nascimento e obrigatoria.");
        }
        if (dto.getDtNasc().after(new Date())) {
            throw new IllegalArgumentException("Data de nascimento invalida.");
        }
        if (dto.getRm() == null){
            dto.setRm(String.format("%05d", RANDOM.nextInt(100000)));
        }
    }

    private String normalizarRg(String valor) {
        return valor == null ? null : valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
    }
}
