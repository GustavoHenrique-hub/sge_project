package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.dto.gestao.TurmaDTO;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.repository.gestao.DisciplinaRepository;
import com.enterprise.repository.gestao.TurmaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DisciplinaService {

    @Inject
    private DisciplinaRepository repository;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public DisciplinaDTO criar(DisciplinaDTO dto) {
        validar(dto);
        DisciplinaEntity entity = new DisciplinaEntity(dto);
        repository.save(entity);
        return new DisciplinaDTO(entity);
    }

    public void validar(DisciplinaDTO dto){
        if (dto == null) {
            throw new IllegalArgumentException("Disciplina invalida.");
        }
        if (dto == null || dto.getDescricao() == null || dto.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatoria.");
        }
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            dto.setCodigo(String.format("%06d", RANDOM.nextInt(1_000_000)));
        }
    }

    public List<DisciplinaEntity> findAll() {
        return repository.findAll();
    }

    public Optional<DisciplinaEntity> findById(Long id) {
        return repository.findById(id);
    }
}