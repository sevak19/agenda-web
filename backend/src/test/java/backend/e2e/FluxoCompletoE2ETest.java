package backend.e2e;

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

import java.util.Map;

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
        String profissionalJson = """
                {
                    "nome": "Dr. Ricardo Alves",
                    "telefone": "11977777777",
                    "endereco": "Av. Paulista, 1000",
                    "categoria": "MEDICO"
                }
                """;

        MvcResult resultProfissional = mockMvc.perform(post("/profissionais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(profissionalJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Dr. Ricardo Alves")))
                .andReturn();

        Long profissionalId = objectMapper
                .readTree(resultProfissional.getResponse().getContentAsString())
                .get("id").asLong();

        // ─── PASSO 2: Criar atendimento ───────────────────────────────
        String atendimentoJson = """
                {
                    "descricao": "Consulta cardiológica",
                    "observacoes": "Paciente com histórico de hipertensão",
                    "dataHora": "2026-06-15T10:00:00",
                    "profissionalSaude": { "id": %d }
                }
                """.formatted(profissionalId);

        MvcResult resultAtendimento = mockMvc.perform(post("/atendimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(atendimentoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.descricao", is("Consulta cardiológica")))
                .andReturn();

        Long atendimentoId = objectMapper
                .readTree(resultAtendimento.getResponse().getContentAsString())
                .get("id").asLong();

        // ─── PASSO 3: Criar exame ─────────────────────────────────────
        String exameJson = """
                {
                    "nome": "Eletrocardiograma",
                    "resultado": "Ritmo sinusal normal",
                    "dataRealizacao": "2026-06-15",
                    "laboratorio": "Cardio Lab",
                    "atendimento": { "id": %d }
                }
                """.formatted(atendimentoId);

        mockMvc.perform(post("/exames")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exameJson))
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
                .andExpect(jsonPath("$", hasSize(0)));
    }
}