package backend.controller;

import backend.entity.Atendimento;
import backend.repository.AtendimentoRepository;
import backend.repository.ProfissionalSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atendimentos")
@RequiredArgsConstructor
public class AtendimentoController {

    private final AtendimentoRepository repository;
    private final ProfissionalSaudeRepository profissionalRepository;

    @GetMapping
    public List<Atendimento> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atendimento> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profissional/{profissionalId}")
    public List<Atendimento> listarPorProfissional(@PathVariable Long profissionalId) {
        return repository.findByProfissionalSaudeId(profissionalId);
    }

    @PostMapping
    public ResponseEntity<Atendimento> criar(@RequestBody Atendimento atendimento) {
        if (atendimento.getProfissionalSaude() == null || atendimento.getProfissionalSaude().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Long profissionalId = atendimento.getProfissionalSaude().getId();

        return profissionalRepository.findById(profissionalId).map(profissional -> {
            atendimento.setProfissionalSaude(profissional);

            if (atendimento.getExamesLaboratorio() != null) {
                atendimento.getExamesLaboratorio().forEach(exame -> exame.setAtendimento(atendimento));
            }

            return ResponseEntity.ok(repository.save(atendimento));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atendimento> atualizar(@PathVariable Long id,
            @RequestBody Atendimento dados) {
        return repository.findById(id).map(atendimento -> {
            atendimento.setDataHora(dados.getDataHora());
            atendimento.setDescricao(dados.getDescricao());
            atendimento.setObservacoes(dados.getObservacoes());
            return ResponseEntity.ok(repository.save(atendimento));
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