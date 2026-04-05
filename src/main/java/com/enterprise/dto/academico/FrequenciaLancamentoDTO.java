package com.enterprise.dto.academico;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.dto.gestao.TurmaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
/**
 * DTO usado para transportar dados de FrequenciaLancamentoDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class FrequenciaLancamentoDTO {

    private Long boletimId;
    private Long frequenciaId;
    private AlunoDTO aluno;
    private TurmaDTO turma;
    private DisciplinaDTO disciplina;
    private BigDecimal frequencia1;
    private BigDecimal frequencia2;
    private BigDecimal frequencia3;
    private BigDecimal frequencia4;
    private boolean editando;
}
