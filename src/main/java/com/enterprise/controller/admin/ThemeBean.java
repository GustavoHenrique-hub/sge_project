package com.enterprise.controller.admin;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
/**
 * Bean JSF que concentra as acoes da tela de ThemeBean e conversa com a camada de servico.
 */

@Named("themeBean")
@SessionScoped
public class ThemeBean implements Serializable {
    private boolean darkTheme = true;
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public boolean isDarkTheme() {
        return darkTheme;
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void toggleTheme() {
        darkTheme = !darkTheme;
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public String getBodyClass() {
        return darkTheme ? "theme-dark" : "theme-light";
    }
}
