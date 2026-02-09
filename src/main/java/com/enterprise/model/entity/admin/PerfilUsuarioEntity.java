package com.enterprise.model.entity.admin;

import com.enterprise.dto.admin.PerfilUsuarioDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "acesso_usuario")
public class PerfilUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "acesso_id")
    private PerfilEntity acesso;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    private String situacao;

    public PerfilUsuarioEntity(PerfilUsuarioDTO perfilUsuarioDTO){
        this.id = perfilUsuarioDTO.getId();
        if(perfilUsuarioDTO.getAcesso() != null){
            this.acesso = new PerfilEntity(perfilUsuarioDTO.getAcesso());
        }
        if(perfilUsuarioDTO.getUsuario() != null){
            this.usuario = new UsuarioEntity(perfilUsuarioDTO.getUsuario());
        }
    }
}
