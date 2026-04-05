package com.enterprise.repository.academico;

import com.enterprise.model.entity.academico.BoletimEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;
/**
 * Repository que centraliza consultas e operacoes de persistencia relacionadas a HistoricoRepository.
 */

@RequestScoped
public class HistoricoRepository {

    @Inject
    private BoletimRepository boletimRepository;
    /**
     * Executa uma operacao de persistencia ou consulta usada pelo restante do modulo.
     */

    public List<BoletimEntity> findBoletinsByAluno(Long alunoId) {
        return boletimRepository.findByAluno(alunoId);
    }
}
