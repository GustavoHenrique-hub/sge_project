package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.repository.gestao.AlunoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AlunoService {

    @Inject
    private AlunoRepository repository;

    @Transactional
    public AlunoDTO criar(AlunoDTO dto) {
        validar(dto);
        AlunoEntity entity = new AlunoEntity(dto);
        repository.save(entity);
        return new AlunoDTO(entity);
    }

    public List<AlunoEntity> findAll() {
        return repository.findAll();
    }

    public List<AlunoEntity> buscar(String nome, String cpf, String status) {
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
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF e obrigatorio.");
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
    }
}