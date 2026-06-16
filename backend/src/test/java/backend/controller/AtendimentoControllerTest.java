package backend.controller;

import backend.entity.*;
import backend.repository.AtendimentoRepository;
import backend.repository.ProfissionalSaudeRepository;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalRepository;

    private ProfissionalSaude profissional;
    private Atendimento atendimento;

    @BeforeEach
    void setUp() {
        atendimentoRepository.deleteAll();
        profissionalRepository.deleteAll();

        profissional = new ProfissionalSaude();
        profissional.setNome("Dra. Ana Paula");
        profissional.setCategoria(Categoria.PSICOLOGO);
        profissionalRepository.save(profissional);

        atendimento = new Atendimento();
        atendimento.setProfissionalSaude(profissional);
        atendimento.setDataHora(LocalDateTime.now());
        atendimento.setDescricao("Consulta inicial");
        atendimentoRepository.save(atendimento);
    }

    @Test
    @DisplayName("GET /atendimentos - deve listar todos")
    void deveListarTodos() throws Exception {
        mockMvc.perform(get("/atendimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /atendimentos/profissional/{id} - deve listar por profissional")
    void deveListarPorProfissional() throws Exception {
        mockMvc.perform(get("/atendimentos/profissional/{id}", profissional.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descricao", is("Consulta inicial")));
    }

    @Test
    @DisplayName("POST /atendimentos - deve criar com profissional válido")
    void deveCriarAtendimento() throws Exception {
        Atendimento novo = new Atendimento();
        novo.setProfissionalSaude(profissional);
        novo.setDataHora(LocalDateTime.now());
        novo.setDescricao("Nova consulta");

        mockMvc.perform(post("/atendimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.descricao", is("Nova consulta")));
    }

    @Test
    @DisplayName("POST /atendimentos - deve retornar 400 com profissional inválido")
    void deveRetornar400ComProfissionalInvalido() throws Exception {
        ProfissionalSaude inexistente = new ProfissionalSaude();
        inexistente.setId(999L);

        Atendimento novo = new Atendimento();
        novo.setProfissionalSaude(inexistente);
        novo.setDataHora(LocalDateTime.now());

        mockMvc.perform(post("/atendimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /atendimentos/{id} - deve remover atendimento")
    void deveDeletarAtendimento() throws Exception {
        mockMvc.perform(delete("/atendimentos/{id}", atendimento.getId()))
                .andExpect(status().isNoContent());
    }
}