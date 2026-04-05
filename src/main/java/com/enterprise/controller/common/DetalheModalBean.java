package com.enterprise.controller.common;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * Bean JSF que concentra as acoes da tela de DetalheModalBean e conversa com a camada de servico.
 */

@Named("detalheModalBean")
@ViewScoped
@Getter
@Setter
public class DetalheModalBean implements Serializable {

    private String titulo = "Detalhes";
    private String includePath = "/components/modal/details/emptyDetalhe.xhtml";
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void abrir(String titulo, String includePath) {
        this.titulo = titulo;
        this.includePath = includePath;
        FacesContext.getCurrentInstance()
                .getPartialViewContext()
                .getEvalScripts()
                .add("bootstrap.Modal.getOrCreateInstance(document.getElementById('modalDetalhes')).show();");
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void limpar() {
        titulo = "Detalhes";
        includePath = "/components/modal/details/emptyDetalhe.xhtml";
    }
}
