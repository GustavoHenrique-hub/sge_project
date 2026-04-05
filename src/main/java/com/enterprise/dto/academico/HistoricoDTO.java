package com.enterprise.dto.academico;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.model.entity.academico.HistoricoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
/**
 * DTO usado para transportar dados de HistoricoDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class HistoricoDTO {

    private AlunoDTO aluno;
    private List<HistoricoDisciplinaDTO> disciplinas = new ArrayList<>();
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public HistoricoDTO(HistoricoEntity entity) {
        this.aluno = entity.getAluno();
        this.disciplinas = entity.getDisciplinas();
    }
}
