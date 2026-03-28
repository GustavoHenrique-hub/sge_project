package com.enterprise.model.entity.admin;

import com.enterprise.dto.admin.UsuarioDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;

    private String login;

    private String senha;

    public UsuarioEntity(UsuarioDTO user) {
        this.id = user.getId();
        this.usuario = user.getUsuario();
        this.login = user.getLogin();
        this.senha = user.getSenha();
    }

    @PreUpdate
    public void preUpdate() {
        this.usuario = this.usuario.toUpperCase();
        this.login = this.login.toUpperCase();
    }

    @PrePersist
    public void prePersist() {
        if (this.usuario != null) {
            this.usuario = this.usuario.toUpperCase();
        }
        if (this.login != null) {
            this.login = this.login.toUpperCase();
        }
    }
}