package com.enterprise.model.entity.gestao;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.dto.gestao.ProfissionalDTO;
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
@Table(name = "profissional")
public class ProfissionalEntity {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "rm", nullable = false, unique = true, length = 5, updatable = false)
    private String rm;

    @Column(name = "cpf", nullable = false, unique = true, length = 11)
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

    public ProfissionalEntity(ProfissionalDTO profissional) {
        this.id = profissional.getId();
        this.rm = profissional.getRm();
        this.cpf = profissional.getCpf();
        this.rg = profissional.getRg();
        this.nome = profissional.getNome();
        this.dtNasc = profissional.getDtNasc();
        this.email = profissional.getEmail();
        this.telefone = profissional.getTelefone();
    }

    @PreUpdate
    public void preUpdate() {
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("\\D", "");
        }
        if (this.nome != null) {
            this.nome = this.nome.toUpperCase();
        }
        if (this.rg != null) {
            this.rg = this.rg.toUpperCase();
        }
    }

    @PrePersist
    private void prePersist() {
        if (id == null) {
            id = generateId();
        }
        if (rm == null || rm.isBlank()) {
            rm = String.format("%05d", RANDOM.nextInt(100000));
        }
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("\\D", "");
        }
        if (this.nome != null) {
            this.nome = this.nome.toUpperCase();
        }
        if (this.rg != null) {
            this.rg = this.rg.toUpperCase();
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

