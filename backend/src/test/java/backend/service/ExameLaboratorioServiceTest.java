package backend.service;

import backend.entity.Atendimento;
import backend.entity.ExameLaboratorio;
import backend.repository.AtendimentoRepository;
import backend.repository.ExameLaboratorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExameLaboratorioServiceTest {

    @Mock
    private ExameLaboratorioRepository repository;

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @InjectMocks
    private ExameLaboratorioService service;

    private Atendimento atendimento;
    private ExameLaboratorio exame;

    @BeforeEach
    void setUp() {
        atendimento = new Atendimento();
        atendimento.setId(1L);

        exame = new ExameLaboratorio();
        exame.setId(1L);
        exame.setNome("Hemograma");
        exame.setResultado("Normal");
        exame.setDataRealizacao(LocalDate.now());
        exame.setLaboratorio("Lab Central");
        exame.setAtendimento(atendimento);
    }

    @Test
    @DisplayName("listarTodos - deve retornar lista de exames")
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(exame));

        List<ExameLaboratorio> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Hemograma");
    }

    @Test
    @DisplayName("buscarPorId - deve retornar exame quando existir")
    void deveBuscarPorIdExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(exame));

        Optional<ExameLaboratorio> resultado = service.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Hemograma");
    }

    @Test
    @DisplayName("listarPorAtendimento - deve retornar exames do atendimento")
    void deveListarPorAtendimento() {
        when(repository.findByAtendimentoId(1L)).thenReturn(List.of(exame));

        List<ExameLaboratorio> resultado = service.listarPorAtendimento(1L);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("criar - deve criar exame com atendimento válido")
    void deveCriarExameComAtendimentoValido() {
        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(atendimento));
        when(repository.save(exame)).thenReturn(exame);

        Optional<ExameLaboratorio> resultado = service.criar(exame);

        assertThat(resultado).isPresent();
        verify(repository).save(exame);
    }

    @Test
    @DisplayName("criar - deve retornar vazio com atendimento inválido")
    void deveRetornarVazioComAtendimentoInvalido() {
        Atendimento inexistente = new Atendimento();
        inexistente.setId(999L);
        exame.setAtendimento(inexistente);

        when(atendimentoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ExameLaboratorio> resultado = service.criar(exame);

        assertThat(resultado).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("criar - deve retornar vazio quando atendimento for null")
    void deveRetornarVazioComAtendimentoNull() {
        exame.setAtendimento(null);

        Optional<ExameLaboratorio> resultado = service.criar(exame);

        assertThat(resultado).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar - deve atualizar exame existente")
    void deveAtualizarExameExistente() {
        ExameLaboratorio dados = new ExameLaboratorio();
        dados.setNome("Glicemia");
        dados.setResultado("Alterado");
        dados.setDataRealizacao(LocalDate.now());
        dados.setLaboratorio("Lab Norte");

        when(repository.findById(1L)).thenReturn(Optional.of(exame));
        when(repository.save(exame)).thenReturn(exame);

        Optional<ExameLaboratorio> resultado = service.atualizar(1L, dados);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Glicemia");
        assertThat(resultado.get().getLaboratorio()).isEqualTo("Lab Norte");
    }

    @Test
    @DisplayName("deletar - deve retornar true quando exame existir")
    void deveDeletarExameExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.deletar(1L);

        assertThat(resultado).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("deletar - deve retornar false quando exame não existir")
    void deveRetornarFalseAoDeletarInexistente() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean resultado = service.deletar(999L);

        assertThat(resultado).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
