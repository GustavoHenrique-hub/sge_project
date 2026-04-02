package com.enterprise.controller.admin;

import com.enterprise.dto.admin.UsuarioDTO;
import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.service.admin.UsuarioService;
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

@Named("usuarioBean")
@ViewScoped
@Getter
@Setter
public class UsuarioBean implements Serializable {

    @Inject
    private UsuarioService service;

    private UsuarioDTO form = new UsuarioDTO();
    private List<UsuarioEntity> usuarios = new ArrayList<>();

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    public void salvar() {
        try {
            if (form.getId() == null) {
                service.criar(form);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário criado.");
            } else {
                service.atualizar(form);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário atualizado.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void editar(UsuarioEntity u) {
        // Carrega no formulário (pra modal abrir preenchida)
        form.setId(u.getId());
        form.setLogin(u.getLogin());
        form.setUsuario(u.getUsuario());
        form.setSessionTimeout(u.getSessionTimeout());
    }

    public void remover(Long id) {
        try {
            service.remover(id);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário removido.");
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void limparFormulario() {
        form = new UsuarioDTO();
    }

    private void recarregarLista() {
        usuarios = service.listar();
    }

    public List<UsuarioEntity> completeUsuario(String query) {
        String formatUsuario = query == null ? "" : query.toLowerCase();
        return service.listar()
                .stream()
                .filter(usuario -> usuario.getLogin() != null
                        && usuario.getLogin().toLowerCase().startsWith(formatUsuario))
                .collect(Collectors.toList());
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}
