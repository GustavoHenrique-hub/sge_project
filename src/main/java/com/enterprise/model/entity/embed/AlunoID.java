package com.enterprise.model.entity.embed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
/**
 * Entidade JPA que representa os dados persistidos de AlunoID no banco.
 */

@Getter
@Setter
@NoArgsConstructor
public class AlunoID implements Serializable {

    private Long id;
    private String rm;
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    public AlunoID(Long id, String rm) {
        this.id = id;
        this.rm = rm;
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
        AlunoID alunoID = (AlunoID) o;
        return Objects.equals(id, alunoID.id) && Objects.equals(rm, alunoID.rm);
    }
    /**
     * Executa a responsabilidade principal deste metodo dentro da classe.
     */

    @Override
    public int hashCode() {
        return Objects.hash(id, rm);
    }
}
