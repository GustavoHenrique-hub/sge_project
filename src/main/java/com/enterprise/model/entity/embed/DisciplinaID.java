package com.enterprise.model.entity.embed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class DisciplinaID implements Serializable {

    private Long id;
    private String rm;

    public DisciplinaID(Long id, String rm) {
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
        DisciplinaID disciplinaID = (DisciplinaID) o;
        return Objects.equals(id, disciplinaID.id) && Objects.equals(rm, disciplinaID.rm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rm);
    }
}
