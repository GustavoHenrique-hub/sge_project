package com.enterprise.controller.admin;

import com.enterprise.dto.admin.UsuarioDTO;
import com.enterprise.dto.gestao.ProfissionalDTO;
import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import com.enterprise.service.admin.UsuarioService;
import com.enterprise.service.gestao.ProfissionalService;
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
    @Inject
    private ProfissionalService profissionalService;

    private UsuarioDTO form = new UsuarioDTO();
    private List<UsuarioEntity> usuarios = new ArrayList<>();
    private ProfissionalEntity profissionalSelecionado;

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    public void salvar() {
        try {
            sincronizarCredenciaisComProfissional();
            if (form.getId() == null) {
                service.criar(form);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuario criado.");
            } else {
                service.atualizar(form);
                addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuario atualizado.");
            }
            limparFormulario();
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void editar(UsuarioEntity usuario) {
        form.setId(usuario.getId());
        form.setSessionTimeout(usuario.getSessionTimeout());
        profissionalSelecionado = usuario.getProfissional() == null
                ? null
                : profissionalService.findById(usuario.getProfissional().getId()).orElse(null);
        sincronizarCredenciaisComProfissional();
    }

    public void remover(Long id) {
        try {
            service.remover(id);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuario removido.");
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void limparFormulario() {
        form = new UsuarioDTO();
        profissionalSelecionado = null;
    }

    public void sincronizarCredenciaisComProfissional() {
        if (profissionalSelecionado == null) {
            form.setProfissional(null);
            form.setLogin(null);
            form.setSenha(null);
            return;
        }

        form.setProfissional(new ProfissionalDTO(profissionalSelecionado));
        String cpfSemPontuacao = profissionalSelecionado.getCpf() == null
                ? null
                : profissionalSelecionado.getCpf().replaceAll("\\D", "");
        form.setLogin(cpfSemPontuacao);
        form.setSenha(cpfSemPontuacao == null || cpfSemPontuacao.isBlank() ? null : cpfSemPontuacao + "_@ABC");
    }

    public String getLoginGerado() {
        return cpfSemPontuacaoSelecionado();
    }

    public String getSenhaGerada() {
        String cpfSemPontuacao = cpfSemPontuacaoSelecionado();
        return cpfSemPontuacao == null || cpfSemPontuacao.isBlank() ? null : cpfSemPontuacao + "_@ABC";
    }

    private void recarregarLista() {
        usuarios = service.listar();
    }

    public List<UsuarioEntity> completeUsuario(String query) {
        String termo = query == null ? "" : query.toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");
        return service.listar()
                .stream()
                .filter(usuario -> correspondeBuscaUsuario(usuario, termo, termoNumerico))
                .collect(Collectors.toList());
    }

    public List<ProfissionalEntity> completeProfissionalUsuario(String query) {
        String termo = query == null ? "" : query.trim().toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");
        return profissionalService.findAll()
                .stream()
                .filter(profissional -> correspondeBuscaProfissional(profissional, termo, termoNumerico))
                .collect(Collectors.toList());
    }

    private boolean correspondeBuscaUsuario(UsuarioEntity usuario, String termo, String termoNumerico) {
        if (usuario == null) {
            return false;
        }
        if (termo.isBlank() && termoNumerico.isBlank()) {
            return true;
        }

        boolean loginCorresponde = usuario.getLogin() != null && usuario.getLogin().contains(termoNumerico);
        boolean nomeCorresponde = usuario.getNomeProfissional() != null
                && usuario.getNomeProfissional().toLowerCase().contains(termo);
        boolean rmCorresponde = usuario.getRmProfissional() != null
                && usuario.getRmProfissional().toLowerCase().contains(termo);
        boolean cpfCorresponde = usuario.getCpfProfissional() != null
                && usuario.getCpfProfissional().contains(termoNumerico);
        return loginCorresponde || nomeCorresponde || rmCorresponde || cpfCorresponde;
    }

    private boolean correspondeBuscaProfissional(ProfissionalEntity profissional, String termo, String termoNumerico) {
        if (profissional == null) {
            return false;
        }
        if (termo.isBlank() && termoNumerico.isBlank()) {
            return true;
        }

        boolean nomeCorresponde = profissional.getNome() != null
                && profissional.getNome().toLowerCase().contains(termo);
        boolean rmCorresponde = profissional.getRm() != null
                && profissional.getRm().toLowerCase().contains(termo);
        boolean cpfCorresponde = profissional.getCpf() != null
                && profissional.getCpf().contains(termoNumerico);
        return nomeCorresponde || rmCorresponde || cpfCorresponde;
    }

    private String cpfSemPontuacaoSelecionado() {
        if (profissionalSelecionado == null || profissionalSelecionado.getCpf() == null) {
            return null;
        }
        return profissionalSelecionado.getCpf().replaceAll("\\D", "");
    }

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}
