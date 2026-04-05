package com.enterprise.model.entity.embed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
/**
 * Entidade JPA que representa os dados persistidos de DisciplinaID no banco.
 */

@Getter
@Setter
@NoArgsConstructor
public class DisciplinaID implements Serializable {

    private Long id;
    private String codigo;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public DisciplinaID(Long id, String codigo) {
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
        DisciplinaID disciplinaID = (DisciplinaID) o;
        return Objects.equals(id, disciplinaID.id) && Objects.equals(codigo, disciplinaID.codigo);
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    @Override
    public int hashCode() {
        return Objects.hash(id, codigo);
    }
}
