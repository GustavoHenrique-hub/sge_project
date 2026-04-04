package com.enterprise.model.entity.academico;

import com.enterprise.dto.academico.FrequenciaDTO;
import com.enterprise.model.entity.gestao.DisciplinaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(
        name = "frequencia",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_frequencia_boletim_disciplina", columnNames = {"boletim_id", "disciplina_id", "disciplina_codigo"})
        },
        indexes = {
                @Index(name = "idx_frequencia_boletim", columnList = "boletim_id"),
                @Index(name = "idx_frequencia_disciplina", columnList = "disciplina_id, disciplina_codigo")
        }
)
public class FrequenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "boletim_id", nullable = false, foreignKey = @ForeignKey(name = "fk_frequencia_boletim"))
    private BoletimEntity boletim;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "disciplina_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(
                    name = "disciplina_codigo",
                    referencedColumnName = "codigo",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_frequencia_disciplina")
            )
    })
    private DisciplinaEntity disciplina;

    @Column(name = "frequencia_1", precision = 5, scale = 2)
    private BigDecimal frequencia1;

    @Column(name = "frequencia_2", precision = 5, scale = 2)
    private BigDecimal frequencia2;

    @Column(name = "frequencia_3", precision = 5, scale = 2)
    private BigDecimal frequencia3;

    @Column(name = "frequencia_4", precision = 5, scale = 2)
    private BigDecimal frequencia4;

    public FrequenciaEntity(FrequenciaDTO dto) {
        this.id = dto.getId();
        if (dto.getBoletim() != null) {
            this.boletim = new BoletimEntity(dto.getBoletim());
        }
        if (dto.getDisciplina() != null) {
            this.disciplina = new DisciplinaEntity(dto.getDisciplina());
        }
        this.frequencia1 = dto.getFrequencia1();
        this.frequencia2 = dto.getFrequencia2();
        this.frequencia3 = dto.getFrequencia3();
        this.frequencia4 = dto.getFrequencia4();
    }
}
