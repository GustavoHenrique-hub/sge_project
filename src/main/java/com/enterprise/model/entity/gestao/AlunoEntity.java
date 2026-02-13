package com.enterprise.model.entity.gestao;

import com.enterprise.dto.gestao.AlunoDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "aluno")
public class AlunoEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nome;
        private Integer idade;

        private String cpf;
        private String rg;

        public AlunoEntity(AlunoDTO aluno) {
            this.id = aluno.getId();
            this.nome = aluno.getNome();
            this.idade = aluno.getIdade();
            this.cpf = aluno.getCpf();
            this.rg = aluno.getRg();
        }

}
