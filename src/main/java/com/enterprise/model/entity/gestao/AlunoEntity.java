package com.enterprise.model.entity.gestao;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.model.entity.embed.AlunoID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(AlunoID.class)
@Table(name = "aluno")
public class AlunoEntity {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Id
    @Column(name = "rm", nullable = false, unique = true, length = 5, updatable = false)
    private String rm;

    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "rg", nullable = false, unique = true, length = 20)
    private String rg;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Temporal(TemporalType.DATE)
    @Column(name = "dt_nasc", nullable = false)
    private Date dtNasc;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    public AlunoEntity(AlunoDTO aluno) {
        this.id = aluno.getId();
        this.rm = aluno.getRm();
        this.cpf = aluno.getCpf();
        this.rg = aluno.getRg();
        this.nome = aluno.getNome();
        this.dtNasc = aluno.getDtNasc();
        this.email = aluno.getEmail();
        this.telefone = aluno.getTelefone();
    }

    @PreUpdate
    private void preUpdate() {
        this.nome = this.nome.toUpperCase();
    }

    @PrePersist
    private void prePersist() {
        if (id == null) {
            id = generateId();
        }
        if (rm == null || rm.isBlank()) {
            rm = String.format("%05d", RANDOM.nextInt(100000));
        }
        if (this.nome != null) {
            this.nome = this.nome.toUpperCase();
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
