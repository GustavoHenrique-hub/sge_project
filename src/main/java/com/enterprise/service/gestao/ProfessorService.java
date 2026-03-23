package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.ProfessorDTO;
import com.enterprise.model.entity.gestao.ProfessorEntity;
import com.enterprise.repository.gestao.ProfessorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProfessorService {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Inject
    private ProfessorRepository repository;

    @Transactional
    public ProfessorDTO criar(ProfessorDTO dto) {
        validar(dto);
        ProfessorEntity entity = new ProfessorEntity(dto);
        repository.save(entity);
        return new ProfessorDTO(entity);
    }

    public List<ProfessorEntity> findAll() {
        return repository.findAll();
    }

    public List<ProfessorEntity> buscar(String nome, String cpf, String rm, String status) {
        return repository.findByFilters(nome, cpf, rm);
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
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }
        if (dto.getRg() == null || dto.getRg().isBlank()) {
            throw new IllegalArgumentException("RG é obrigatório.");
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
