package com.enterprise.controller.common;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
/**
 * Bean JSF que concentra as acoes da tela de LayoutBean e conversa com a camada de servico.
 */

@Named("layoutBean")
@RequestScoped
public class LayoutBean implements Serializable {
    private String search;
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public String getSearch() {
        return search;
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void setSearch(String search) {
        this.search = search;
    }
}
