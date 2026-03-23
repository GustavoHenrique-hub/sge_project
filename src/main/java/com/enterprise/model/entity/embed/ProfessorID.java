package com.enterprise.model.entity.embed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ProfessorID implements Serializable {

    private Long id;
    private String rm;

    public ProfessorID(Long id, String rm) {
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
        ProfessorID professorID = (ProfessorID) o;
        return Objects.equals(id, professorID.id) && Objects.equals(rm, professorID.rm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rm);
    }
}
