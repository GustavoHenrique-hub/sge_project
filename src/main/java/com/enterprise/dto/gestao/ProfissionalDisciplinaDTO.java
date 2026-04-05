package com.enterprise.dto.gestao;

import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.model.entity.gestao.AlunoTurmaEntity;
import com.enterprise.model.entity.gestao.ProfissionalDisciplinaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO usado para transportar dados de ProfissionalDisciplinaDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class ProfissionalDisciplinaDTO {

    private Long id;
    private ProfissionalDTO profissional;
    private DisciplinaDTO disciplina;
    private SituacaoDTO situacao;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public ProfissionalDisciplinaDTO(ProfissionalDisciplinaEntity profissionalDisciplina) {
        this.id = profissionalDisciplina.getId();
        if (profissionalDisciplina.getProfissional() != null) {
            this.profissional = new ProfissionalDTO(profissionalDisciplina.getProfissional());
        }
        if (profissionalDisciplina.getDisciplina() != null) {
            this.disciplina = new DisciplinaDTO(profissionalDisciplina.getDisciplina());
        }
        if (profissionalDisciplina.getSituacao() != null) {
            this.situacao = new SituacaoDTO(profissionalDisciplina.getSituacao());
        }
    }
}
