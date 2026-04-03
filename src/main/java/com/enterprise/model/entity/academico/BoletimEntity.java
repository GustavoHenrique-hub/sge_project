package com.enterprise.model.entity.academico;

import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.academico.BoletimDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.entity.gestao.AlunoEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
        name = "aluno_disciplina",
        indexes = {
                @Index(name = "idx_boletim_aluno", columnList = "aluno_id, aluno_rm"),
                @Index(name = "idx_boletim_disciplina", columnList = "disciplina_id, disciplina_codigo")
        }
)
public class BoletimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "aluno_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "aluno_rm",
                    referencedColumnName = "rm",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_aluno_disciplina_aluno")
            )
    })
    private AlunoEntity aluno;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "disciplina_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "disciplina_codigo",
                    referencedColumnName = "codigo",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_aluno_disciplina_disciplina")
            )
    })
    private DisciplinaEntity disciplina;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "situacao_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_aluno_disciplina_situacao")
    )
    private SituacaoEntity situacao;

    public BoletimEntity(BoletimDTO dto) {
        this.id = dto.getId();
        if (dto.getAluno() != null) {
            this.aluno = new AlunoEntity(dto.getAluno());
        }
        if (dto.getDisciplina() != null) {
            this.disciplina = new DisciplinaEntity(dto.getDisciplina());
        }
        if (dto.getSituacao() != null) {
            SituacaoDTO situacaoDTO = new SituacaoDTO();
            situacaoDTO.setId(dto.getSituacao().getId());
            situacaoDTO.setSituacao(dto.getSituacao().getSituacao());
            situacaoDTO.setDescricao(dto.getSituacao().getDescricao());
            this.situacao = new SituacaoEntity(situacaoDTO);
        }
    }
}
