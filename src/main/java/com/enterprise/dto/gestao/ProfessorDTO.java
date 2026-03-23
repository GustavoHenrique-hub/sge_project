package com.enterprise.dto.gestao;

import com.enterprise.model.entity.gestao.ProfessorEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProfessorDTO {

    private Long id;
    private String rm;
    private String nome;
    private String cpf;
    private String rg;
    private Date dtNasc;
    private String email;
    private String telefone;

    public ProfessorDTO(ProfessorEntity professor) {
        this.id = professor.getId();
        this.rm = professor.getRm();
        this.cpf = professor.getCpf();
        this.rg = professor.getRg();
        this.nome = professor.getNome();
        this.dtNasc = professor.getDtNasc();
        this.email = professor.getEmail();
        this.telefone = professor.getTelefone();
    }
}