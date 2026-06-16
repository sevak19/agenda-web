package backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "exames_laboratorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExameLaboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    private String resultado;

    private LocalDate dataRealizacao;

    private String laboratorio;

    @ManyToOne
    @JoinColumn(name = "atendimento_id", nullable = false)
    @ToString.Exclude
    private Atendimento atendimento;
}