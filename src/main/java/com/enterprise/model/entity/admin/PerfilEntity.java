package com.enterprise.model.entity.admin;

import com.enterprise.dto.admin.PerfilDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Entidade JPA que representa os dados persistidos de PerfilEntity no banco.
 */

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
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public PerfilEntity(PerfilDTO perfil) {
        this.id = perfil.getId();
        this.perfil = perfil.getPerfil();
        this.descricao = perfil.getDescricao();
    }
}