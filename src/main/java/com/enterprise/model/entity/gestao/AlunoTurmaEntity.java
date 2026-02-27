package com.enterprise.model.entity.gestao;

import com.enterprise.dto.admin.SituacaoDTO;
import com.enterprise.dto.gestao.AlunoTurmaDTO;
import com.enterprise.model.entity.admin.SituacaoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "aluno_turma")
public class AlunoTurmaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "aluno_id", referencedColumnName = "id"),
            @JoinColumn(name = "aluno_rm", referencedColumnName = "rm")
    })
    private AlunoEntity aluno;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "turma_id", referencedColumnName = "id"),
            @JoinColumn(name = "turma_codigo", referencedColumnName = "codigo")
    })
    private TurmaEntity turma;

    @ManyToOne(optional = false)
    @JoinColumn(name = "situacao_id")
    private SituacaoEntity situacao;

    public AlunoTurmaEntity(AlunoTurmaDTO dto) {
        this.id = dto.getId();
        if (dto.getAluno() != null) {
            this.aluno = new AlunoEntity(dto.getAluno());
        }
        if (dto.getTurma() != null) {
            this.turma = new TurmaEntity(dto.getTurma());
        }
        if (dto.getSituacao() != null) {
            SituacaoDTO situacaoDTO = new SituacaoDTO();
            situacaoDTO.setId(dto.getSituacao().getId());
            situacaoDTO.setSituacao(dto.getSituacao().getSituacao());
            situacaoDTO.setDescricao(dto.getSituacao().getDescricao());
            this.situacao = new SituacaoEntity(situacaoDTO);
        }
    }
}
