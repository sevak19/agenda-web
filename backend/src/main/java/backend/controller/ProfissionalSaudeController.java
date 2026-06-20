package backend.controller;

import backend.entity.ProfissionalSaude;
import backend.service.ProfissionalSaudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
@RequiredArgsConstructor
public class ProfissionalSaudeController {

    private final ProfissionalSaudeService service;

    @GetMapping
    public List<ProfissionalSaude> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalSaude> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<ProfissionalSaude> buscarPorNome(@RequestParam String nome) {
        return service.buscarPorNome(nome);
    }

    @PostMapping
    public ProfissionalSaude criar(@RequestBody ProfissionalSaude profissional) {
        return service.criar(profissional);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalSaude> atualizar(@PathVariable Long id,
                                                        @RequestBody ProfissionalSaude dados) {
        return service.atualizar(id, dados)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!service.deletar(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
