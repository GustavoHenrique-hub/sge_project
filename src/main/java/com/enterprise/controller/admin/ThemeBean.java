package com.enterprise.controller.admin;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class ThemeBean implements Serializable {
    private boolean darkTheme = true;

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void toggleTheme() {
        darkTheme = !darkTheme;
    }

    public String getBodyClass() {
        return darkTheme ? "theme-dark" : "theme-light";
    }
}