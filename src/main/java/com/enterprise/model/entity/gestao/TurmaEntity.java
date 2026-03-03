package com.enterprise.model.entity.gestao;

import com.enterprise.dto.gestao.TurmaDTO;
import com.enterprise.model.entity.embed.TurmaID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(TurmaID.class)
@Table(name = "turma")
public class TurmaEntity {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Id
    @Column(name = "codigo", nullable = false, unique = true, length = 6, updatable = false)
    private String codigo;

    @Column(name = "turma", nullable = false, length = 80)
    private String turma;

    public TurmaEntity(TurmaDTO dto) {
        this.id = dto.getId();
        this.codigo = dto.getCodigo();
        this.turma = dto.getTurma();
    }

    @PrePersist
    private void prePersist() {
        if (id == null) {
            id = generateId();
        }
        if (codigo == null || codigo.isBlank()) {
            codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        }
    }

    private Long generateId() {
        long generated;
        do {
            generated = RANDOM.nextLong();
        } while (generated <= 0);
        return generated;
    }
}
