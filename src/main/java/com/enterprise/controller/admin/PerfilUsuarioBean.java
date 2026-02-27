package com.enterprise.controller.admin;

import com.enterprise.dto.admin.PerfilDTO;
import com.enterprise.dto.admin.PerfilUsuarioDTO;
import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.admin.UsuarioDTO;
import com.enterprise.model.entity.admin.PerfilEntity;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.service.admin.PerfilService;
import com.enterprise.service.admin.PerfilUsuarioService;
import com.enterprise.service.admin.SituacaoService;
import com.enterprise.service.admin.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;

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
    @Inject
    private SituacaoService situacaoService;
    @Inject
    private UsuarioService usuarioService;
    private PerfilUsuarioDTO perfilUsuarioDTO = new PerfilUsuarioDTO();
    private List<PerfilUsuarioDTO> perfilUsuarios = new ArrayList<>();
    private Long perfilId;
    private Long usuarioId;
    private Long situacaoId;
    private UsuarioEntity filtroUsuario;
    private PerfilEntity filtroPerfil;
    private SituacaoEntity filtroSituacao;
    private List<String> situacao = new ArrayList<>();


    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        perfilUsuarios = service.listarDTO();
        PrimeFaces current = PrimeFaces.current();
        if (current != null) {
            current.ajax().update("formLista:listaUsers", "growl");
        }
    }

    public void limparFormulario() {
        perfilUsuarioDTO = new PerfilUsuarioDTO();
        perfilId = null;
        usuarioId = null;
        situacaoId = null;
    }

    public String situacaoSeverity(String situacao) {
        if (situacao == null) {
            return "info";
        }
        return switch (situacao.toUpperCase()) {
            case "ATIVO" -> "badge-success";
            case "INATIVO" -> "badge-danger";
            default -> "warning";
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
                if (situacaoId != null){
                    SituacaoEntity situacao = situacaoService.findById(situacaoId)
                            .orElseThrow(() -> new IllegalArgumentException("Situacao inválida."));
                    perfilUsuarioDTO.setSituacao(new SituacaoDTO(situacao));
                } else {
                    perfilUsuarioDTO.setSituacao(null);
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
        try {
            String login = filtroUsuario == null ? null : filtroUsuario.getLogin();
            Long perfilId = filtroPerfil == null ? null : filtroPerfil.getId();
            Long situacaoId = filtroSituacao == null ? null : filtroSituacao.getId();
            boolean filtroVazio = (login == null || login.isBlank())
                    && perfilId == null
                    && situacaoId == null;
            if (filtroVazio) {
                perfilUsuarios = service.listarDTO();
            } else {
                perfilUsuarios = service.listarPorFiltros(login, perfilId, situacaoId);
            }
            int total = perfilUsuarios == null ? 0 : perfilUsuarios.size();
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + total + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void ativarVinculo(PerfilUsuarioDTO dto) {
        try {
            service.ativarVinculo(dto.getId());
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Vínculo ativado.");
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void inativarVinculo(PerfilUsuarioDTO dto) {
        try {
            service.inativarVinculo(dto.getId());
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Vínculo inativado.");
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void excluirVinculo(PerfilUsuarioDTO dto) {
        try {
            service.excluirVinculoSeInativo(dto.getId());
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Vínculo excluído.");
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public boolean isSituacaoInativa(PerfilUsuarioDTO dto) {
        String situacao = dto == null || dto.getSituacao() == null ? null : dto.getSituacao().getSituacao();
        return situacao != null && situacao.equalsIgnoreCase("INATIVO");
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }

    public List<UsuarioEntity> completeUsuario(String query) {
        String formatUsuario = query == null ? "" : query.toLowerCase();
        return usuarioService.listar()
                .stream()
                .filter(usuario -> usuario.getLogin() != null
                        && usuario.getLogin().toLowerCase().startsWith(formatUsuario))
                .collect(Collectors.toList());
    }

    public List<String> completeSituacao(String query) {
        String formatSituacao = query == null ? "" : query.toLowerCase();
        List<String> situacaoList = new ArrayList<>();
        List<PerfilUsuarioDTO> situacoes = service.listarDTO();
        for (PerfilUsuarioDTO perfilUsuarioDTO : situacoes) {
            if (perfilUsuarioDTO.getSituacao() != null && perfilUsuarioDTO.getSituacao().getSituacao() != null) {
                situacaoList.add(perfilUsuarioDTO.getSituacao().getSituacao());
            }
        }

        return situacaoList.stream().filter(t -> t.toLowerCase().startsWith(formatSituacao)).collect(Collectors.toList());
    }

}