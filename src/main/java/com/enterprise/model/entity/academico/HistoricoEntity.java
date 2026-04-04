package com.enterprise.model.entity.academico;

import com.enterprise.dto.academico.HistoricoDisciplinaDTO;
import com.enterprise.dto.gestao.AlunoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HistoricoEntity {

    private AlunoDTO aluno;
    private List<HistoricoDisciplinaDTO> disciplinas = new ArrayList<>();
}
