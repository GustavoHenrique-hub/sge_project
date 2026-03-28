package com.enterprise.model.entity.gestao;

import com.enterprise.dto.gestao.DisciplinaDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "disciplina")
public class DisciplinaEntity {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 6, updatable = false)
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 80)
    private String descricao;

    public DisciplinaEntity(DisciplinaDTO dto) {
        this.id = dto.getId();
        this.codigo = dto.getCodigo();
        this.descricao = dto.getDescricao();
    }

    @PrePersist
    private void prePersist() {
        if (id == null) {
            id = generateId();
        }
        if (codigo == null || codigo.isBlank()) {
            codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        }
        if (descricao != null && !descricao.isBlank()) {
            descricao = descricao.toUpperCase();
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
