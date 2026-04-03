package com.enterprise.model.entity.gestao;

import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.dto.gestao.ProfissionalDisciplinaDTO;
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
        name = "profissional_disciplina",
        indexes = {
                @Index(name = "idx_profissional_disciplina_profissional", columnList = "profissional_id, profissional_rm"),
                @Index(name = "idx_profissional_disciplina_disciplina", columnList = "disciplina_id, disciplina_codigo")
        }
)
public class ProfissionalDisciplinaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "profissional_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "profissional_rm",
                    referencedColumnName = "rm",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_profissional_disciplina_profissional")
            )
    })
    private ProfissionalEntity profissional;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "disciplina_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "disciplina_codigo",
                    referencedColumnName = "codigo",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_profissional_disciplina_disciplina")
            )
    })
    private DisciplinaEntity disciplina;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "situacao_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_profissional_disciplina_situacao")
    )
    private SituacaoEntity situacao;

    public ProfissionalDisciplinaEntity(ProfissionalDisciplinaDTO dto) {
        this.id = dto.getId();
        if (dto.getProfissional() != null) {
            this.profissional = new ProfissionalEntity(dto.getProfissional());
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

