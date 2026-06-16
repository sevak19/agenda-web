package backend.e2e;

import backend.entity.Categoria;
import backend.entity.ProfissionalSaude;
import backend.entity.Atendimento;
import backend.entity.ExameLaboratorio;
import backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("E2E - Fluxo completo: Profissional → Atendimento → Exame")
class FluxoCompletoE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExameLaboratorioRepository exameRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalRepository;

    @BeforeEach
    void limparBanco() {
        exameRepository.deleteAll();
        atendimentoRepository.deleteAll();
        profissionalRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar profissional → atendimento → exame e consultar tudo")
    void fluxoCompleto() throws Exception {

        // ─── PASSO 1: Criar profissional ──────────────────────────────
        ProfissionalSaude profissional = new ProfissionalSaude();
        profissional.setNome("Dr. Ricardo Alves");
        profissional.setTelefone("11977777777");
        profissional.setEndereco("Av. Paulista, 1000");
        profissional.setCategoria(Categoria.MEDICO);

        MvcResult resultProfissional = mockMvc.perform(post("/profissionais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissional)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Dr. Ricardo Alves")))
                .andReturn();

        Long profissionalId = objectMapper
                .readTree(resultProfissional.getResponse().getContentAsString())
                .get("id").asLong();

        // ─── PASSO 2: Criar atendimento vinculado ao profissional ─────
        ProfissionalSaude ref = new ProfissionalSaude();
        ref.setId(profissionalId);

        Atendimento atendimento = new Atendimento();
        atendimento.setProfissionalSaude(ref);
        atendimento.setDataHora(LocalDateTime.now());
        atendimento.setDescricao("Consulta cardiológica");
        atendimento.setObservacoes("Paciente com histórico de hipertensão");

        MvcResult resultAtendimento = mockMvc.perform(post("/atendimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atendimento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.descricao", is("Consulta cardiológica")))
                .andReturn();

        Long atendimentoId = objectMapper
                .readTree(resultAtendimento.getResponse().getContentAsString())
                .get("id").asLong();

        // ─── PASSO 3: Criar exame vinculado ao atendimento ────────────
        Atendimento refAtendimento = new Atendimento();
        refAtendimento.setId(atendimentoId);

        ExameLaboratorio exame = new ExameLaboratorio();
        exame.setAtendimento(refAtendimento);
        exame.setNome("Eletrocardiograma");
        exame.setResultado("Ritmo sinusal normal");
        exame.setDataRealizacao(LocalDate.now());
        exame.setLaboratorio("Cardio Lab");

        mockMvc.perform(post("/exames")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exame)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Eletrocardiograma")));

        // ─── PASSO 4: Verificar atendimentos do profissional ──────────
        mockMvc.perform(get("/atendimentos/profissional/{id}", profissionalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descricao", is("Consulta cardiológica")));

        // ─── PASSO 5: Verificar exames do atendimento ─────────────────
        mockMvc.perform(get("/exames/atendimento/{id}", atendimentoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Eletrocardiograma")));

        // ─── PASSO 6: Deletar atendimento e verificar cascade ─────────
        mockMvc.perform(delete("/atendimentos/{id}", atendimentoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/exames/atendimento/{id}", atendimentoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // cascade deletou os exames
    }
}