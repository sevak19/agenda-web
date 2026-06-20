package backend.controller;

import backend.entity.Atendimento;
import backend.service.AtendimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atendimentos")
@RequiredArgsConstructor
public class AtendimentoController {

    private final AtendimentoService service;

    @GetMapping
    public List<Atendimento> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atendimento> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profissional/{profissionalId}")
    public List<Atendimento> listarPorProfissional(@PathVariable Long profissionalId) {
        return service.listarPorProfissional(profissionalId);
    }

    @PostMapping
    public ResponseEntity<Atendimento> criar(@RequestBody Atendimento atendimento) {
        return service.criar(atendimento)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atendimento> atualizar(@PathVariable Long id,
            @RequestBody Atendimento dados) {
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
