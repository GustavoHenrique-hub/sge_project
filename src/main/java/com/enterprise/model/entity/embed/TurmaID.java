package com.enterprise.model.entity.embed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
/**
 * Entidade JPA que representa os dados persistidos de TurmaID no banco.
 */

@Getter
@Setter
@NoArgsConstructor
public class TurmaID implements Serializable {

    private Long id;
    private String codigo;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public TurmaID(Long id, String codigo) {
        this.id = id;
        this.codigo = codigo;
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TurmaID turmaID = (TurmaID) o;
        return Objects.equals(id, turmaID.id) && Objects.equals(codigo, turmaID.codigo);
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    @Override
    public int hashCode() {
        return Objects.hash(id, codigo);
    }
}
