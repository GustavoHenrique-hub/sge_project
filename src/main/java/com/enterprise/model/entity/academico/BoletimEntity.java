package com.enterprise.model.entity.academico;

import com.enterprise.dto.academico.BoletimDTO;
import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.TurmaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
        name = "boletim",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_boletim_aluno_turma", columnNames = {"aluno_id", "aluno_rm", "turma_id", "turma_codigo"})
        },
        indexes = {
                @Index(name = "idx_boletim_aluno", columnList = "aluno_id, aluno_rm"),
                @Index(name = "idx_boletim_turma", columnList = "turma_id, turma_codigo")
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
                    foreignKey = @ForeignKey(name = "fk_boletim_aluno")
            )
    })
    private AlunoEntity aluno;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "turma_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "turma_codigo",
                    referencedColumnName = "codigo",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_boletim_turma")
            )
    })
    private TurmaEntity turma;

    @OneToMany(mappedBy = "boletim", fetch = FetchType.LAZY)
    private List<NotaEntity> notas = new ArrayList<>();

    @OneToMany(mappedBy = "boletim", fetch = FetchType.LAZY)
    private List<FrequenciaEntity> frequencias = new ArrayList<>();

    public BoletimEntity(BoletimDTO dto) {
        this.id = dto.getId();
        if (dto.getAluno() != null) {
            this.aluno = new AlunoEntity(dto.getAluno());
        }
        if (dto.getTurma() != null) {
            this.turma = new TurmaEntity(dto.getTurma());
        }
    }
}
