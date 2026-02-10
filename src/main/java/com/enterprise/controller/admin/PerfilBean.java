package com.enterprise.controller.admin;

import com.enterprise.dto.admin.PerfilDTO;
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

@Named("perfilBean")
@ViewScoped
@Getter
@Setter
public class PerfilBean implements Serializable {

    @Inject
    private PerfilService service;

    private PerfilDTO perfilDTO = new PerfilDTO();
    private List<PerfilEntity> perfils = new ArrayList<>();


    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        perfils = service.findAll();
    }

    public void limparFormulario() {
        perfilDTO = new PerfilDTO();
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

}
