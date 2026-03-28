package com.enterprise.controller.common;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Named("detalheModalBean")
@ViewScoped
@Getter
@Setter
public class DetalheModalBean implements Serializable {

    private String titulo = "Detalhes";
    private String includePath = "/components/modal/details/emptyDetalhe.xhtml";

    public void abrir(String titulo, String includePath) {
        this.titulo = titulo;
        this.includePath = includePath;
        FacesContext.getCurrentInstance()
                .getPartialViewContext()
                .getEvalScripts()
                .add("bootstrap.Modal.getOrCreateInstance(document.getElementById('modalDetalhes')).show();");
    }

    public void limpar() {
        titulo = "Detalhes";
        includePath = "/components/modal/details/emptyDetalhe.xhtml";
    }
}
