package com.enterprise.dto.gestao;

import com.enterprise.model.entity.gestao.AlunoEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class AlunoDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer idade;

    private String cpf;
    private String rg;

    public AlunoDTO(AlunoEntity aluno) {
        this.id = aluno.getId();
        this.nome = aluno.getNome();
        this.idade = aluno.getIdade();
        this.cpf = aluno.getCpf();
        this.rg = aluno.getRg();
    }

}
