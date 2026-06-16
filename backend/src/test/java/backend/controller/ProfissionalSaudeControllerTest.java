package backend.controller;

import backend.entity.Categoria;
import backend.entity.ProfissionalSaude;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfissionalSaudeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfissionalSaudeRepository repository;

    private ProfissionalSaude profissional;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        profissional = new ProfissionalSaude();
        profissional.setNome("Dr. João Silva");
        profissional.setTelefone("11999999999");
        profissional.setEndereco("Rua das Flores, 123");
        profissional.setCategoria(Categoria.MEDICO);
        repository.save(profissional);
    }

    @Test
    @DisplayName("GET /profissionais - deve retornar lista")
    void deveListarTodos() throws Exception {
        mockMvc.perform(get("/profissionais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Dr. João Silva")));
    }

    @Test
    @DisplayName("GET /profissionais/{id} - deve retornar profissional")
    void deveBuscarPorId() throws Exception {
        mockMvc.perform(get("/profissionais/{id}", profissional.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dr. João Silva")))
                .andExpect(jsonPath("$.categoria", is("MEDICO")));
    }

    @Test
    @DisplayName("GET /profissionais/{id} - deve retornar 404 quando não encontrado")
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/profissionais/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /profissionais - deve criar novo profissional")
    void deveCriarNovoProfissional() throws Exception {
        ProfissionalSaude novo = new ProfissionalSaude();
        novo.setNome("Dra. Maria Souza");
        novo.setTelefone("11988888888");
        novo.setCategoria(Categoria.FISIOTERAPEUTA);

        mockMvc.perform(post("/profissionais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Dra. Maria Souza")));
    }

    @Test
    @DisplayName("PUT /profissionais/{id} - deve atualizar profissional")
    void deveAtualizarProfissional() throws Exception {
        profissional.setNome("Dr. João Atualizado");

        mockMvc.perform(put("/profissionais/{id}", profissional.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissional)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dr. João Atualizado")));
    }

    @Test
    @DisplayName("DELETE /profissionais/{id} - deve remover profissional")
    void deveDeletarProfissional() throws Exception {
        mockMvc.perform(delete("/profissionais/{id}", profissional.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/profissionais/{id}", profissional.getId()))
                .andExpect(status().isNotFound());
    }
}