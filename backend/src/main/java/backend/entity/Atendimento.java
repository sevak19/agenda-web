package backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "atendimentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDateTime dataHora;

    private String descricao;

    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "profissional_saude_id", nullable = false)
    @ToString.Exclude
    private ProfissionalSaude profissionalSaude;

    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @ToString.Exclude
    private List<ExameLaboratorio> examesLaboratorio;
}