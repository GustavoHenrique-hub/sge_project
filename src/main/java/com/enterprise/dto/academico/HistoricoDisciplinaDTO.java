package com.enterprise.dto.academico;

import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.dto.gestao.TurmaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
/**
 * DTO usado para transportar dados de HistoricoDisciplinaDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class HistoricoDisciplinaDTO {

    private TurmaDTO turma;
    private DisciplinaDTO disciplina;
    private String mediaFinal;
    private String situacaoNota;
    private BigDecimal presencaFinal;
    private String situacaoFrequencia;
    private String situacaoFinal;
}
