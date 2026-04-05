package com.enterprise.dto.gestao;

import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.model.entity.gestao.AlunoTurmaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO usado para transportar dados de AlunoTurmaDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class AlunoTurmaDTO {

    private Long id;
    private AlunoDTO aluno;
    private TurmaDTO turma;
    private SituacaoDTO situacao;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public AlunoTurmaDTO(AlunoTurmaEntity alunoTurma) {
        this.id = alunoTurma.getId();
        if (alunoTurma.getAluno() != null) {
            this.aluno = new AlunoDTO(alunoTurma.getAluno());
        }
        if (alunoTurma.getTurma() != null) {
            this.turma = new TurmaDTO(alunoTurma.getTurma());
        }
        if (alunoTurma.getSituacao() != null) {
            this.situacao = new SituacaoDTO(alunoTurma.getSituacao());
        }
    }
}