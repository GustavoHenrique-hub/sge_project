package com.enterprise.model.entity.academico;

import com.enterprise.dto.academico.NotaDTO;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import com.enterprise.model.enums.academico.ConceitoNota;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
        name = "nota",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_nota_boletim_disciplina", columnNames = {"boletim_id", "disciplina_id", "disciplina_codigo"})
        },
        indexes = {
                @Index(name = "idx_nota_boletim", columnList = "boletim_id"),
                @Index(name = "idx_nota_disciplina", columnList = "disciplina_id, disciplina_codigo")
        }
)
public class NotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "boletim_id", nullable = false, foreignKey = @ForeignKey(name = "fk_nota_boletim"))
    private BoletimEntity boletim;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "disciplina_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "disciplina_codigo",
                    referencedColumnName = "codigo",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_nota_disciplina")
            )
    })
    private DisciplinaEntity disciplina;

    @Enumerated(EnumType.STRING)
    @Column(name = "nota_1")
    private ConceitoNota nota1;

    @Enumerated(EnumType.STRING)
    @Column(name = "nota_2")
    private ConceitoNota nota2;

    @Enumerated(EnumType.STRING)
    @Column(name = "nota_3")
    private ConceitoNota nota3;

    @Enumerated(EnumType.STRING)
    @Column(name = "nota_4")
    private ConceitoNota nota4;

    public NotaEntity(NotaDTO dto) {
        this.id = dto.getId();
        if (dto.getBoletim() != null) {
            this.boletim = new BoletimEntity(dto.getBoletim());
        }
        if (dto.getDisciplina() != null) {
            this.disciplina = new DisciplinaEntity(dto.getDisciplina());
        }
        this.nota1 = ConceitoNota.fromValue(dto.getNota1());
        this.nota2 = ConceitoNota.fromValue(dto.getNota2());
        this.nota3 = ConceitoNota.fromValue(dto.getNota3());
        this.nota4 = ConceitoNota.fromValue(dto.getNota4());
    }
}
