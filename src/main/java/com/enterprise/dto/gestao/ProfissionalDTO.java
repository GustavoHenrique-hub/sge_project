package com.enterprise.dto.gestao;

import com.enterprise.model.entity.gestao.ProfissionalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
/**
 * DTO usado para transportar dados de ProfissionalDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class ProfissionalDTO {

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

    public ProfissionalDTO(ProfissionalEntity profissional) {
        this.id = profissional.getId();
        this.rm = profissional.getRm();
        this.cpf = profissional.getCpf();
        this.rg = profissional.getRg();
        this.nome = profissional.getNome();
        this.dtNasc = profissional.getDtNasc();
        this.email = profissional.getEmail();
        this.telefone = profissional.getTelefone();
    }
}
