package com.enterprise.controller.admin;

import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.service.admin.SituacaoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Bean JSF que concentra as acoes da tela de SituacaoBean e conversa com a camada de servico.
 */

@Named("situacaoBean")
@ViewScoped
@Getter
@Setter
public class SituacaoBean implements Serializable {

    @Inject
    private SituacaoService service;

    private SituacaoEntity form = new SituacaoEntity();
    private SituacaoEntity filtroSituacao;
    private List<SituacaoEntity> situacao = new ArrayList<>();
    /**
     * Inicializa o estado da tela ou da classe assim que a instancia fica disponivel.
     */

    @PostConstruct
    public void init() {
        recarregarLista();
    }
    /**
     * Limpa os campos do formulario para preparar um novo cadastro ou nova consulta.
     */

    public void limparFormulario() {
        form = new SituacaoEntity();
    }
    /**
     * Recarrega a lista exibida na interface para refletir o estado atual dos dados.
     */

    private void recarregarLista() {
        situacao = service.findAll();
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<SituacaoEntity> completeSituacao(String query) {
        String formatSituacao = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(situacao -> situacao.getSituacao() != null
                        && situacao.getSituacao().toLowerCase().startsWith(formatSituacao))
                .collect(Collectors.toList());
    }
}