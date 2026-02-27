package com.enterprise.model.entity.gestao;

import com.enterprise.dto.gestao.AlunoDTO;
import com.enterprise.model.entity.embed.AlunoID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @PrePersist
    private void prePersist() {
        if (rm == null || rm.isBlank()) {
            rm = String.format("%05d", RANDOM.nextInt(100000));
        }
    }

}
