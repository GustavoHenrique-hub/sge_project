package com.enterprise.dto.gestao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessorDTO {

    private Long id;
    private String nome;
    private String disciplina;
    private String contato;
    private String situacao;

    public ProfessorDTO(Long id, String nome, String disciplina, String contato, String situacao) {
        this.id = id;
        this.nome = nome;
        this.disciplina = disciplina;
        this.contato = contato;
        this.situacao = situacao;
    }
}
