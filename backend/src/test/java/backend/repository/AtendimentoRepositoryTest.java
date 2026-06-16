package backend.repository;

import backend.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AtendimentoRepositoryTest {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalRepository;

    private ProfissionalSaude profissional;

    @BeforeEach
    void setUp() {
        profissional = new ProfissionalSaude();
        profissional.setNome("Dra. Ana Paula");
        profissional.setCategoria(Categoria.PSICOLOGO);
        profissionalRepository.save(profissional);

        Atendimento atendimento = new Atendimento();
        atendimento.setProfissionalSaude(profissional);
        atendimento.setDataHora(LocalDateTime.now());
        atendimento.setDescricao("Consulta de rotina");
        atendimentoRepository.save(atendimento);
    }

    @Test
    @DisplayName("Deve listar atendimentos por profissional")
    void deveListarAtendimentosPorProfissional() {
        List<Atendimento> resultado = atendimentoRepository
                .findByProfissionalSaudeId(profissional.getId());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDescricao()).isEqualTo("Consulta de rotina");
    }

    @Test
    @DisplayName("Deve listar atendimentos por intervalo de data")
    void deveListarAtendimentosPorIntervaloDeData() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);

        List<Atendimento> resultado = atendimentoRepository
                .findByDataHoraBetween(inicio, fim);

        assertThat(resultado).hasSize(1);
    }
}