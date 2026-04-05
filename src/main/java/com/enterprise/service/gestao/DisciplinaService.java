package com.enterprise.service.gestao;

import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.dto.gestao.TurmaDTO;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import com.enterprise.repository.gestao.DisciplinaRepository;
import com.enterprise.repository.gestao.TurmaRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de DisciplinaService.
 */

@RequestScoped
public class DisciplinaService {

    @Inject
    private DisciplinaRepository repository;

    private static final SecureRandom RANDOM = new SecureRandom();
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public DisciplinaDTO criar(DisciplinaDTO dto) {
        validar(dto);
        DisciplinaEntity entity = new DisciplinaEntity(dto);
        repository.save(entity);
        return new DisciplinaDTO(entity);
    }
    /**
     * Atualiza o registro existente aplicando as regras de negocio desta camada.
     */

    public DisciplinaDTO atualizar(DisciplinaDTO dto) {
        validar(dto);
        if (dto.getId() == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Disciplina invalida para atualizacao.");
        }
        DisciplinaEntity existente = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Disciplina nao encontrada."));
        existente.setDescricao(dto.getDescricao());
        DisciplinaEntity merged = repository.update(existente);
        return new DisciplinaDTO(merged);
    }
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public void validar(DisciplinaDTO dto){
        if (dto == null) {
            throw new IllegalArgumentException("Disciplina invalida.");
        }
        if (dto == null || dto.getDescricao() == null || dto.getDescricao().isBlank()) {
            throw new IllegalArgumentException("DescriÃƒÂ§ÃƒÂ£o ÃƒÂ© obrigatoria.");
        }
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            dto.setCodigo(String.format("%06d", RANDOM.nextInt(1_000_000)));
        }
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<DisciplinaEntity> findAll() {
        return repository.findAll();
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<DisciplinaEntity> findById(Long id) {
        return repository.findById(id);
    }
}
