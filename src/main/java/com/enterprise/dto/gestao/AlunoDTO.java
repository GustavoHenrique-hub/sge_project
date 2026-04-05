package com.enterprise.dto.gestao;

import com.enterprise.model.entity.gestao.AlunoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
/**
 * DTO usado para transportar dados de AlunoDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class AlunoDTO {

    private Long id;
    private String rm;
    private String nome;
    private String cpf;
    private String rg;
    private Date dtNasc;
    private String email;
    private String telefone;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public AlunoDTO(AlunoEntity aluno) {
        this.id = aluno.getId();
        this.rm = aluno.getRm();
        this.nome = aluno.getNome();
        this.cpf = aluno.getCpf();
        this.rg = aluno.getRg();
        this.dtNasc = aluno.getDtNasc();
        this.email = aluno.getEmail();
        this.telefone = aluno.getTelefone();
    }

}