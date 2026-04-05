package com.enterprise.dto.academico;

import com.enterprise.dto.gestao.DisciplinaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
/**
 * DTO usado para transportar dados de BoletimDisciplinaDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class BoletimDisciplinaDTO {

    private DisciplinaDTO disciplina;
    private String nota1;
    private String nota2;
    private String nota3;
    private String nota4;
    private BigDecimal frequencia1;
    private BigDecimal frequencia2;
    private BigDecimal frequencia3;
    private BigDecimal frequencia4;
    private Double mediaNumerica;
    private String mediaFinal;
    private String situacaoNota;
    private BigDecimal presencaFinal;
    private String situacaoFrequencia;
    private String situacaoFinal;
}
