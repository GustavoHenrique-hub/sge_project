package com.enterprise.dto.gestao;

import com.enterprise.model.entity.gestao.TurmaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO usado para transportar dados de TurmaDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class TurmaDTO {

    private Long id;
    private String codigo;
    private String turma;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public TurmaDTO(TurmaEntity turma) {
        this.id = turma.getId();
        this.codigo = turma.getCodigo();
        this.turma = turma.getTurma();
    }
}