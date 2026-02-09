package com.enterprise.model.entity.admin;

import com.enterprise.dto.PerfilDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "acesso")
public class PerfilEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeAcesso;
    private String descricao;

    public PerfilEntity(PerfilDTO acesso) {
        this.id = acesso.getId();
        this.nomeAcesso = acesso.getNomeAcesso();
        this.descricao = acesso.getDescricao();
    }
}
