package com.enterprise.controller.admin;

import com.enterprise.dto.admin.PerfilDTO;
import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.dto.admin.UsuarioDTO;
import com.enterprise.model.entity.admin.PerfilEntity;
import com.enterprise.service.admin.PerfilService;
import com.enterprise.service.admin.PerfilUsuarioService;
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

@Named("perfilUsuarioBean")
@ViewScoped
@Getter
@Setter
public class PerfilUsuarioBean implements Serializable {

    @Inject
    private PerfilUsuarioService service;
    @Inject
    private PerfilService perfilService;
    private PerfilUsuarioDTO perfilUsuarioDTO = new PerfilUsuarioDTO();
    private List<PerfilUsuarioDTO> perfilUsuarios = new ArrayList<>();
    private Long perfilId;
    private Long usuarioId;
    private String filtroUsuario;
    private Long filtroPerfilId;


    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        perfilUsuarios = service.listarDTO();
    }

    public void limparFormulario() {
        perfilUsuarioDTO = new PerfilUsuarioDTO();
        perfilId = null;
        usuarioId = null;
    }

    public String situacaoSeverity(String situacao) {
        if (situacao == null) {
            return "info";
        }
        return switch (situacao.toUpperCase()) {
            case "ATIVO" -> "success";
            case "INATIVO" -> "danger";
            default -> "warning";
        };
    }

    public String situacaoBadgeClass(String situacao) {
        if (situacao == null) {
            return "badge-pill badge-muted";
        }
        return switch (situacao.toUpperCase()) {
            case "ATIVO" -> "badge-pill badge-success";
            case "INATIVO" -> "badge-pill badge-danger";
            default -> "badge-pill badge-warning";
        };
    }

    public void vincular(){
        try{
            if(perfilUsuarioDTO.getId() == null){
                if (usuarioId != null) {
                    UsuarioDTO usuario = new UsuarioDTO();
                    usuario.setId(usuarioId);
                    perfilUsuarioDTO.setUsuario(usuario);
                } else {
                    perfilUsuarioDTO.setUsuario(null);
                }
                if (perfilId != null) {
                    PerfilEntity perfil = perfilService.findById(perfilId)
                            .orElseThrow(() -> new IllegalArgumentException("Perfil inválido."));
                    perfilUsuarioDTO.setPerfil(new PerfilDTO(perfil));
                } else {
                    perfilUsuarioDTO.setPerfil(null);
                }
                service.vincular(perfilUsuarioDTO);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário criado.");
            }
            recarregarLista();
            limparFormulario();
        }catch (Exception e){
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void filtrar() {
        perfilUsuarios = service.listarPorFiltros(filtroUsuario, filtroPerfilId);
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public List<String> completeUsuario(String query) {
        String queryLowerCase = query.toLowerCase();
        List<String> usuarioList = new ArrayList<>();
        List<PerfilUsuarioDTO> usuarios = service.listarDTO();
        for (PerfilUsuarioDTO country : usuarios) {
            usuarioList.add(country.getUsuario().getLogin());
        }

        return usuarioList.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
    }

}
