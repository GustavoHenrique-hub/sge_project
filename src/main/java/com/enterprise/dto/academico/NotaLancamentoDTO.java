package com.enterprise.dto.academico;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.dto.gestao.TurmaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO usado para transportar dados de NotaLancamentoDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class NotaLancamentoDTO {

    private Long boletimId;
    private Long notaId;
    private AlunoDTO aluno;
    private TurmaDTO turma;
    private DisciplinaDTO disciplina;
    private String nota1;
    private String nota2;
    private String nota3;
    private String nota4;
    private boolean editando;
}
