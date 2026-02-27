package com.enterprise.controller.gestao;

import com.enterprise.dto.gestao.ProfessorDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("professorBean")
@ViewScoped
@Getter
@Setter
public class ProfessorBean implements Serializable {

    private String filtroNome;
    private List<ProfessorDTO> professores = new ArrayList<>();
    private ProfessorDTO detalheSelecionado;

    @PostConstruct
    public void init() {
        recarregarLista();
    }

    private void recarregarLista() {
        professores = new ArrayList<>(List.of(
                new ProfessorDTO(1L, "Marcos Lima", "Matematica", "(11) 99999-9999", "ATIVO"),
                new ProfessorDTO(2L, "Carla Souza", "Portugues", "(11) 98888-7777", "ATIVO"),
                new ProfessorDTO(3L, "Roberto Alves", "Historia", "(11) 97777-6666", "INATIVO")
        ));
    }

    public void filtrar() {
        try {
            List<ProfessorDTO> listaBase = new ArrayList<>(List.of(
                    new ProfessorDTO(1L, "Marcos Lima", "Matematica", "(11) 99999-9999", "ATIVO"),
                    new ProfessorDTO(2L, "Carla Souza", "Portugues", "(11) 98888-7777", "ATIVO"),
                    new ProfessorDTO(3L, "Roberto Alves", "Historia", "(11) 97777-6666", "INATIVO")
            ));
            if (filtroNome != null && !filtroNome.isBlank()) {
                professores = listaBase.stream()
                        .filter(p -> p.getNome() != null && p.getNome().toLowerCase().contains(filtroNome.toLowerCase()))
                        .collect(Collectors.toList());
            } else {
                professores = listaBase;
            }
            addMsg(FacesMessage.SEVERITY_INFO, "Sucesso", "Filtro aplicado. Registros: " + professores.size() + ".");
        } catch (Exception e) {
            addMsg(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao filtrar. " + e.getMessage());
        }
    }

    public void detalhar(ProfessorDTO professor) {
        detalheSelecionado = professor;
    }

    public List<String> completeProfessor(String query) {
        String format = query == null ? "" : query.toLowerCase();
        return new ArrayList<>(List.of("Marcos Lima", "Carla Souza", "Roberto Alves")).stream()
                .filter(nome -> nome != null && nome.toLowerCase().startsWith(format))
                .collect(Collectors.toList());
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

    private void addMsg(FacesMessage.Severity severity, String title, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, detail));
    }
}