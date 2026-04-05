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
/**
 * Bean JSF que concentra as acoes da tela de UsuarioBean e conversa com a camada de servico.
 */

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
    /**
     * Inicializa o estado da tela ou da classe assim que a instancia fica disponivel.
     */

    @PostConstruct
    public void init() {
        recarregarLista();
    }
    /**
     * Salva os dados atuais do fluxo e atualiza os elementos que dependem desse resultado.
     */

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
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void editar(UsuarioEntity usuario) {
        form.setId(usuario.getId());
        form.setSessionTimeout(usuario.getSessionTimeout());
        profissionalSelecionado = usuario.getProfissional() == null
                ? null
                : profissionalService.findById(usuario.getProfissional().getId()).orElse(null);
        sincronizarCredenciaisComProfissional();
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public void remover(Long id) {
        try {
            service.remover(id);
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuario removido.");
            recarregarLista();
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }
    /**
     * Limpa os campos do formulario para preparar um novo cadastro ou nova consulta.
     */

    public void limparFormulario() {
        form = new UsuarioDTO();
        profissionalSelecionado = null;
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

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
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public String getLoginGerado() {
        return cpfSemPontuacaoSelecionado();
    }
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    public String getSenhaGerada() {
        String cpfSemPontuacao = cpfSemPontuacaoSelecionado();
        return cpfSemPontuacao == null || cpfSemPontuacao.isBlank() ? null : cpfSemPontuacao + "_@ABC";
    }
    /**
     * Recarrega a lista exibida na interface para refletir o estado atual dos dados.
     */

    private void recarregarLista() {
        usuarios = service.listar();
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<UsuarioEntity> completeUsuario(String query) {
        String termo = query == null ? "" : query.toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");
        return service.listar()
                .stream()
                .filter(usuario -> correspondeBuscaUsuario(usuario, termo, termoNumerico))
                .collect(Collectors.toList());
    }
    /**
     * Monta a lista de sugestoes do autocomplete com base no termo informado pela tela.
     */

    public List<ProfissionalEntity> completeProfissionalUsuario(String query) {
        String termo = query == null ? "" : query.trim().toLowerCase();
        String termoNumerico = query == null ? "" : query.replaceAll("\\D", "");
        return profissionalService.findAll()
                .stream()
                .filter(profissional -> correspondeBuscaProfissional(profissional, termo, termoNumerico))
                .collect(Collectors.toList());
    }
    /**
     * Centraliza a validacao usada para decidir se o registro atende ao filtro informado.
     */

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
    /**
     * Centraliza a validacao usada para decidir se o registro atende ao filtro informado.
     */

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
    /**
     * Executa uma acao da tela e prepara os dados consumidos pelos componentes JSF.
     */

    private String cpfSemPontuacaoSelecionado() {
        if (profissionalSelecionado == null || profissionalSelecionado.getCpf() == null) {
            return null;
        }
        return profissionalSelecionado.getCpf().replaceAll("\\D", "");
    }
    /**
     * Adiciona uma mensagem de retorno para orientar o usuario sobre o resultado da acao.
     */

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}
