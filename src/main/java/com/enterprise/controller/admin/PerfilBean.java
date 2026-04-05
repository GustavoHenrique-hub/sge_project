package com.enterprise.controller.admin;

import com.enterprise.dto.admin.PerfilDTO;
import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.model.entity.admin.PerfilEntity;
import com.enterprise.service.admin.PerfilService;
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
 * Bean JSF que concentra as acoes da tela de PerfilBean e conversa com a camada de servico.
 */

@Named("perfilBean")
@ViewScoped
@Getter
@Setter
public class PerfilBean implements Serializable {

    @Inject
    private PerfilService service;

    private PerfilDTO perfilDTO = new PerfilDTO();
    private List<PerfilEntity> perfils = new ArrayList<>();
    /**
     * Inicializa o estado da tela ou da classe assim que a instancia fica disponivel.
     */


    @PostConstruct
    public void init() {
        recarregarLista();
    }
    /**
     * Recarrega a lista exibida na interface para refletir o estado atual dos dados.
     */

    private void recarregarLista() {
        perfils = service.findAll();
    }
    /**
     * Limpa os campos do formulario para preparar um novo cadastro ou nova consulta.
     */

    public void limparFormulario() {
        perfilDTO = new PerfilDTO();
    }
    /**
     * Adiciona uma mensagem de retorno para orientar o usuario sobre o resultado da acao.
     */

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<PerfilEntity> completePerfil(String query) {
        String formatPerfil = query == null ? "" : query.toLowerCase();
        return service.findAll()
                .stream()
                .filter(perfil -> perfil.getPerfil() != null
                        && perfil.getPerfil().toLowerCase().startsWith(formatPerfil))
                .collect(Collectors.toList());
    }

}