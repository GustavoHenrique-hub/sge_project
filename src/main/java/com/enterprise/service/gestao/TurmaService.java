package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.TurmaDTO;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.repository.gestao.TurmaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TurmaService {

    @Inject
    private TurmaRepository repository;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public TurmaDTO criar(TurmaDTO dto) {
        validar(dto);
        TurmaEntity entity = new TurmaEntity(dto);
        repository.save(entity);
        return new TurmaDTO(entity);
    }

    @Transactional
    public TurmaDTO atualizar(TurmaDTO dto) {
        validar(dto);
        if (dto.getId() == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Turma invalida para atualizacao.");
        }
        TurmaEntity merged = repository.update(new TurmaEntity(dto));
        return new TurmaDTO(merged);
    }

    public void validar(TurmaDTO dto){
        if (dto == null) {
            throw new IllegalArgumentException("Turma invalida.");
        }
        if (dto == null || dto.getTurma() == null || dto.getTurma().isBlank()) {
            throw new IllegalArgumentException("Turma e obrigatoria.");
        }
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            dto.setCodigo(String.format("%06d", RANDOM.nextInt(1_000_000)));
        }
    }

    public List<TurmaEntity> findAll() {
        return repository.findAll();
    }

    public Optional<TurmaEntity> findById(Long id) {
        return repository.findById(id);
    }
}
