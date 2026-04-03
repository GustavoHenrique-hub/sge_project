package com.enterprise.model.entity.embed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ProfissionalID implements Serializable {

    private Long id;
    private String rm;

    public ProfissionalID(Long id, String rm) {
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
        ProfissionalID profissionalID = (ProfissionalID) o;
        return Objects.equals(id, profissionalID.id) && Objects.equals(rm, profissionalID.rm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rm);
    }
}

