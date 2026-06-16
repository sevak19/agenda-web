package backend.repository;

import backend.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ExameLaboratorioRepositoryTest {

    @Autowired
    private ExameLaboratorioRepository exameRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalRepository;

    private Atendimento atendimento;

    @BeforeEach
    void setUp() {
        ProfissionalSaude profissional = new ProfissionalSaude();
        profissional.setNome("Dr. Carlos");
        profissional.setCategoria(Categoria.MEDICO);
        profissionalRepository.save(profissional);

        atendimento = new Atendimento();
        atendimento.setProfissionalSaude(profissional);
        atendimento.setDataHora(LocalDateTime.now());
        atendimentoRepository.save(atendimento);

        ExameLaboratorio exame = new ExameLaboratorio();
        exame.setNome("Hemograma Completo");
        exame.setResultado("Normal");
        exame.setDataRealizacao(LocalDate.now());
        exame.setLaboratorio("Lab Central");
        exame.setAtendimento(atendimento);
        exameRepository.save(exame);
    }

    @Test
    @DisplayName("Deve listar exames por atendimento")
    void deveListarExamesPorAtendimento() {
        List<ExameLaboratorio> resultado = exameRepository
                .findByAtendimentoId(atendimento.getId());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Hemograma Completo");
    }

    @Test
    @DisplayName("Deve buscar exame por nome parcial")
    void deveBuscarExamePorNomeParcial() {
        List<ExameLaboratorio> resultado = exameRepository
                .findByNomeContainingIgnoreCase("hemograma");

        assertThat(resultado).hasSize(1);
    }
}