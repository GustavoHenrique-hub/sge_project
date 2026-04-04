package com.enterprise.repository.academico;

import com.enterprise.model.entity.academico.BoletimEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;

@RequestScoped
public class HistoricoRepository {

    @Inject
    private BoletimRepository boletimRepository;

    public List<BoletimEntity> findBoletinsByAluno(Long alunoId) {
        return boletimRepository.findByAluno(alunoId);
    }
}
