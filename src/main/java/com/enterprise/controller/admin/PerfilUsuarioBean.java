package com.enterprise.controller.admin;

import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.model.entity.admin.PerfilUsuarioEntity;
import com.enterprise.service.admin.PerfilUsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Named("perfilUsuarioBean")
@ViewScoped
@Getter
@Setter
public class PerfilUsuarioBean {

    @Inject
    private PerfilUsuarioService service;

    private PerfilUsuarioDTO perfilUsuarioDTO = new PerfilUsuarioDTO();
    private List<PerfilUsuarioEntity> perfilUsuarios = new ArrayList<>();


    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        perfilUsuarios = service.listar();
    }
}
