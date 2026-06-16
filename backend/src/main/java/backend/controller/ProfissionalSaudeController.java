package backend.controller;

import backend.entity.ProfissionalSaude;
import backend.repository.ProfissionalSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
@RequiredArgsConstructor
public class ProfissionalSaudeController {

    private final ProfissionalSaudeRepository repository;

    @GetMapping
    public List<ProfissionalSaude> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalSaude> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<ProfissionalSaude> buscarPorNome(@RequestParam String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @PostMapping
    public ProfissionalSaude criar(@RequestBody ProfissionalSaude profissional) {
        return repository.save(profissional);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalSaude> atualizar(@PathVariable Long id,
                                                        @RequestBody ProfissionalSaude dados) {
        return repository.findById(id).map(profissional -> {
            profissional.setNome(dados.getNome());
            profissional.setTelefone(dados.getTelefone());
            profissional.setEndereco(dados.getEndereco());
            profissional.setCategoria(dados.getCategoria());
            return ResponseEntity.ok(repository.save(profissional));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}