package com.enterprise.model.entity.gestao;

import com.enterprise.dto.gestao.TurmaDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;
/**
 * Entidade JPA que representa os dados persistidos de TurmaEntity no banco.
 */

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "turma")
public class TurmaEntity {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 6, updatable = false)
    private String codigo;

    @Column(name = "turma", nullable = false, length = 80)
    private String turma;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public TurmaEntity(TurmaDTO dto) {
        this.id = dto.getId();
        this.codigo = dto.getCodigo();
        this.turma = dto.getTurma();
    }
    /**
     * Normaliza os campos necessarios antes de atualizar o registro persistido.
     */

    @PreUpdate
    public void preUpdate() {
        this.turma = this.turma.toUpperCase();
    }
    /**
     * Prepara valores obrigatorios e padroes antes de inserir o registro no banco.
     */

    @PrePersist
    private void prePersist() {
        if (id == null) {
            id = generateId();
        }
        if (codigo == null || codigo.isBlank()) {
            codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        }
        if (turma != null && !turma.isBlank()) {
            turma = turma.toUpperCase();
        }
    }
    /**
     * Gera um identificador positivo para novos registros quando esse valor ainda nao foi definido.
     */

    private Long generateId() {
        long generated;
        do {
            generated = RANDOM.nextLong();
        } while (generated <= 0);
        return generated;
    }
}
