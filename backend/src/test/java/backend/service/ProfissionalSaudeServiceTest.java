package backend.service;

import backend.entity.Categoria;
import backend.entity.ProfissionalSaude;
import backend.repository.ProfissionalSaudeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfissionalSaudeServiceTest {

    @Mock
    private ProfissionalSaudeRepository repository;

    @InjectMocks
    private ProfissionalSaudeService service;

    private ProfissionalSaude profissional;

    @BeforeEach
    void setUp() {
        profissional = new ProfissionalSaude();
        profissional.setId(1L);
        profissional.setNome("Dr. João Silva");
        profissional.setTelefone("11999999999");
        profissional.setEndereco("Rua das Flores, 123");
        profissional.setCategoria(Categoria.MEDICO);
    }

    @Test
    @DisplayName("listarTodos - deve retornar lista de profissionais")
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(profissional));

        List<ProfissionalSaude> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Dr. João Silva");
    }

    @Test
    @DisplayName("buscarPorId - deve retornar profissional quando existir")
    void deveBuscarPorIdExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(profissional));

        Optional<ProfissionalSaude> resultado = service.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Dr. João Silva");
    }

    @Test
    @DisplayName("buscarPorId - deve retornar vazio quando não existir")
    void deveBuscarPorIdInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<ProfissionalSaude> resultado = service.buscarPorId(999L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("criar - deve salvar e retornar profissional")
    void deveCriarProfissional() {
        when(repository.save(profissional)).thenReturn(profissional);

        ProfissionalSaude resultado = service.criar(profissional);

        assertThat(resultado).isEqualTo(profissional);
        verify(repository).save(profissional);
    }

    @Test
    @DisplayName("atualizar - deve atualizar quando profissional existir")
    void deveAtualizarProfissionalExistente() {
        ProfissionalSaude dados = new ProfissionalSaude();
        dados.setNome("Dr. João Atualizado");
        dados.setTelefone("11988888888");
        dados.setEndereco("Nova Rua, 456");
        dados.setCategoria(Categoria.FISIOTERAPEUTA);

        when(repository.findById(1L)).thenReturn(Optional.of(profissional));
        when(repository.save(profissional)).thenReturn(profissional);

        Optional<ProfissionalSaude> resultado = service.atualizar(1L, dados);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Dr. João Atualizado");
        assertThat(resultado.get().getCategoria()).isEqualTo(Categoria.FISIOTERAPEUTA);
    }

    @Test
    @DisplayName("atualizar - deve retornar vazio quando profissional não existir")
    void deveRetornarVazioAoAtualizarInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<ProfissionalSaude> resultado = service.atualizar(999L, profissional);

        assertThat(resultado).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("deletar - deve retornar true quando profissional existir")
    void deveDeletarProfissionalExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.deletar(1L);

        assertThat(resultado).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("deletar - deve retornar false quando profissional não existir")
    void deveRetornarFalseAoDeletarInexistente() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean resultado = service.deletar(999L);

        assertThat(resultado).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
