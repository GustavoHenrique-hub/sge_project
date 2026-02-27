package com.enterprise.model.entity.embed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class AlunoID implements Serializable {

    private Long id;
    private String rm;

    public AlunoID(Long id, String rm) {
        this.id = id;
        this.rm = rm;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(id, rm);
    }
}
