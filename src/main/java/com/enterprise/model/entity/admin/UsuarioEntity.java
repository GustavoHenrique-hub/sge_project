package com.enterprise.model.entity.admin;

import com.enterprise.dto.admin.UsuarioDTO;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Entidade JPA que representa os dados persistidos de UsuarioEntity no banco.
 */

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "profissional_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(name = "profissional_rm", referencedColumnName = "rm", nullable = false)
    })
    private ProfissionalEntity profissional;

    @Column(nullable = false, unique = true, length = 11)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Column(name = "session_timeout")
    private Integer sessionTimeout;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public UsuarioEntity(UsuarioDTO user) {
        this.id = user.getId();
        if (user.getProfissional() != null) {
            this.profissional = new ProfissionalEntity(user.getProfissional());
        }
        this.login = user.getLogin();
        this.senha = user.getSenha();
        this.sessionTimeout = user.getSessionTimeout();
        aplicarCredenciaisPadrao();
    }
    /**
     * Normaliza os campos necessarios antes de atualizar o registro persistido.
     */

    @PreUpdate
    public void preUpdate() {
        aplicarCredenciaisPadrao();
    }
    /**
     * Prepara valores obrigatorios e padroes antes de inserir o registro no banco.
     */

    @PrePersist
    public void prePersist() {
        aplicarCredenciaisPadrao();
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public String getNomeProfissional() {
        return profissional == null ? null : profissional.getNome();
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public String getRmProfissional() {
        return profissional == null ? null : profissional.getRm();
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public String getCpfProfissional() {
        return profissional == null ? null : profissional.getCpf();
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    private void aplicarCredenciaisPadrao() {
        String cpfSemPontuacao = profissional == null || profissional.getCpf() == null
                ? null
                : profissional.getCpf().replaceAll("\\D", "");
        if (cpfSemPontuacao != null && !cpfSemPontuacao.isBlank()) {
            this.login = cpfSemPontuacao;
            this.senha = cpfSemPontuacao + "_@ABC";
        }
    }
}
