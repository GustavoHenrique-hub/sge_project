package com.enterprise.dto.academico;

import com.enterprise.dto.gestao.DisciplinaDTO;
import com.enterprise.model.entity.academico.NotaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO usado para transportar dados de NotaDTO entre a tela, servicos e entidades.
 */

@Getter
@Setter
@NoArgsConstructor
public class NotaDTO {

    private Long id;
    private BoletimDTO boletim;
    private DisciplinaDTO disciplina;
    private String nota1;
    private String nota2;
    private String nota3;
    private String nota4;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public NotaDTO(NotaEntity entity) {
        this.id = entity.getId();
        if (entity.getBoletim() != null) {
            this.boletim = new BoletimDTO();
            this.boletim.setId(entity.getBoletim().getId());
        }
        if (entity.getDisciplina() != null) {
            this.disciplina = new DisciplinaDTO(entity.getDisciplina());
        }
        this.nota1 = entity.getNota1() == null ? null : entity.getNota1().name();
        this.nota2 = entity.getNota2() == null ? null : entity.getNota2().name();
        this.nota3 = entity.getNota3() == null ? null : entity.getNota3().name();
        this.nota4 = entity.getNota4() == null ? null : entity.getNota4().name();
    }
}
