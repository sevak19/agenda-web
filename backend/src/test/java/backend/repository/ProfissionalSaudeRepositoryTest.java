package backend.repository;

import backend.entity.Categoria;
import backend.entity.ProfissionalSaude;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProfissionalSaudeRepositoryTest {

    @Autowired
    private ProfissionalSaudeRepository repository;

    private ProfissionalSaude profissional;

    @BeforeEach
    void setUp() {
        profissional = new ProfissionalSaude();
        profissional.setNome("Dr. João Silva");
        profissional.setTelefone("11999999999");
        profissional.setEndereco("Rua das Flores, 123");
        profissional.setCategoria(Categoria.MEDICO);
        repository.save(profissional);
    }

    @Test
    @DisplayName("Deve salvar e recuperar profissional por ID")
    void deveSalvarERecuperarPorId() {
        var encontrado = repository.findById(profissional.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Dr. João Silva");
    }

    @Test
    @DisplayName("Deve buscar profissional por nome ignorando case")
    void deveBuscarPorNomeIgnorandoCase() {
        List<ProfissionalSaude> resultado = repository
                .findByNomeContainingIgnoreCase("joão");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Dr. João Silva");
    }

    @Test
    @DisplayName("Deve buscar profissional por categoria")
    void deveBuscarPorCategoria() {
        List<ProfissionalSaude> resultado = repository
                .findByCategoria(Categoria.MEDICO);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo(Categoria.MEDICO);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar nome inexistente")
    void deveRetornarVazioQuandoNomeNaoExiste() {
        List<ProfissionalSaude> resultado = repository
                .findByNomeContainingIgnoreCase("Inexistente");

        assertThat(resultado).isEmpty();
    }
}