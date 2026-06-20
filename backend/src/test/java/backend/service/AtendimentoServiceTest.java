package backend.service;

import backend.entity.Atendimento;
import backend.entity.Categoria;
import backend.entity.ProfissionalSaude;
import backend.repository.AtendimentoRepository;
import backend.repository.ProfissionalSaudeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @Mock
    private AtendimentoRepository repository;

    @Mock
    private ProfissionalSaudeRepository profissionalRepository;

    @InjectMocks
    private AtendimentoService service;

    private ProfissionalSaude profissional;
    private Atendimento atendimento;

    @BeforeEach
    void setUp() {
        profissional = new ProfissionalSaude();
        profissional.setId(1L);
        profissional.setNome("Dra. Ana Paula");
        profissional.setCategoria(Categoria.PSICOLOGO);

        atendimento = new Atendimento();
        atendimento.setId(1L);
        atendimento.setProfissionalSaude(profissional);
        atendimento.setDataHora(LocalDateTime.now());
        atendimento.setDescricao("Consulta inicial");
    }

    @Test
    @DisplayName("listarTodos - deve retornar lista de atendimentos")
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(atendimento));

        List<Atendimento> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDescricao()).isEqualTo("Consulta inicial");
    }

    @Test
    @DisplayName("buscarPorId - deve retornar atendimento quando existir")
    void deveBuscarPorIdExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(atendimento));

        Optional<Atendimento> resultado = service.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDescricao()).isEqualTo("Consulta inicial");
    }

    @Test
    @DisplayName("listarPorProfissional - deve retornar atendimentos do profissional")
    void deveListarPorProfissional() {
        when(repository.findByProfissionalSaudeId(1L)).thenReturn(List.of(atendimento));

        List<Atendimento> resultado = service.listarPorProfissional(1L);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("criar - deve criar atendimento com profissional válido")
    void deveCriarAtendimentoComProfissionalValido() {
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(repository.save(atendimento)).thenReturn(atendimento);

        Optional<Atendimento> resultado = service.criar(atendimento);

        assertThat(resultado).isPresent();
        verify(repository).save(atendimento);
    }

    @Test
    @DisplayName("criar - deve retornar vazio com profissional inválido")
    void deveRetornarVazioComProfissionalInvalido() {
        ProfissionalSaude inexistente = new ProfissionalSaude();
        inexistente.setId(999L);
        atendimento.setProfissionalSaude(inexistente);

        when(profissionalRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Atendimento> resultado = service.criar(atendimento);

        assertThat(resultado).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("criar - deve retornar vazio quando profissionalSaude for null")
    void deveRetornarVazioComProfissionalNull() {
        atendimento.setProfissionalSaude(null);

        Optional<Atendimento> resultado = service.criar(atendimento);

        assertThat(resultado).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar - deve atualizar atendimento existente")
    void deveAtualizarAtendimentoExistente() {
        Atendimento dados = new Atendimento();
        dados.setDescricao("Consulta atualizada");
        dados.setDataHora(LocalDateTime.now());
        dados.setObservacoes("Nova observação");

        when(repository.findById(1L)).thenReturn(Optional.of(atendimento));
        when(repository.save(atendimento)).thenReturn(atendimento);

        Optional<Atendimento> resultado = service.atualizar(1L, dados);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDescricao()).isEqualTo("Consulta atualizada");
    }

    @Test
    @DisplayName("deletar - deve retornar true quando atendimento existir")
    void deveDeletarAtendimentoExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.deletar(1L);

        assertThat(resultado).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("deletar - deve retornar false quando atendimento não existir")
    void deveRetornarFalseAoDeletarInexistente() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean resultado = service.deletar(999L);

        assertThat(resultado).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
