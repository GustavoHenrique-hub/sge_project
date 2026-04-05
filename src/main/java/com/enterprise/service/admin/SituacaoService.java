package com.enterprise.service.admin;

import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.repository.admin.SituacaoRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
/**
 * Service responsavel pelas regras de negocio e pelos fluxos principais de SituacaoService.
 */

@RequestScoped
public class SituacaoService {

    @Inject
    private SituacaoRepository repository;
    /**
     * Executa uma parte da regra de negocio e organiza o fluxo principal deste servico.
     */

    public SituacaoEntity criar(SituacaoEntity entity) {
        return repository.save(entity);
    }
    /**
     * Busca um unico registro pelo identificador informado, quando ele existir.
     */

    public Optional<SituacaoEntity> findById(Long id) {
        return repository.findById(id);
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public Optional<SituacaoEntity> findBySituacao(String situacao) {
        return repository.findBySituacao(situacao);
    }
    /**
     * Executa uma consulta filtrada usando os parametros recebidos pelo fluxo atual.
     */

    public List<SituacaoEntity> findByFilters(String situacao) {
        return repository.findByFilters(situacao);
    }
    /**
     * Busca todos os registros dessa entidade no criterio padrao adotado pela aplicacao.
     */

    public List<SituacaoEntity> findAll() {
        return repository.findAll();
    }

}
