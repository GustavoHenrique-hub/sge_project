package com.enterprise.model.entity.admin;

import com.enterprise.dto.admin.PerfilDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "perfil")
public class PerfilEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String perfil;
    private String descricao;

    public PerfilEntity(PerfilDTO perfil) {
        this.id = perfil.getId();
        this.perfil = perfil.getPerfil();
        this.descricao = perfil.getDescricao();
    }
}
