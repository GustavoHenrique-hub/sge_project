package com.enterprise.model.entity.gestao;

import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.dto.gestao.ProfessorDisciplinaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
        name = "professor_disciplina",
        indexes = {
                @Index(name = "idx_professor_disciplina_professor", columnList = "professor_id, professor_rm"),
                @Index(name = "idx_professor_disciplina_disciplina", columnList = "disciplina_id, disciplina_codigo")
        }
)
public class ProfessorDisciplinaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "professor_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "professor_rm",
                    referencedColumnName = "rm",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_professor_disciplina_professor")
            )
    })
    private ProfessorEntity professor;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "disciplina_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "disciplina_codigo",
                    referencedColumnName = "codigo",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_professor_disciplina_disciplina")
            )
    })
    private DisciplinaEntity disciplina;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "situacao_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_professor_disciplina_situacao")
    )
    private SituacaoEntity situacao;

    public ProfessorDisciplinaEntity(ProfessorDisciplinaDTO dto) {
        this.id = dto.getId();
        if (dto.getProfessor() != null) {
            this.professor = new ProfessorEntity(dto.getProfessor());
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
