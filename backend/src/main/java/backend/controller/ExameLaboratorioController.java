package backend.controller;

import backend.entity.ExameLaboratorio;
import backend.repository.AtendimentoRepository;
import backend.repository.ExameLaboratorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exames")
@RequiredArgsConstructor
public class ExameLaboratorioController {

    private final ExameLaboratorioRepository repository;
    private final AtendimentoRepository atendimentoRepository;

    @GetMapping
    public List<ExameLaboratorio> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameLaboratorio> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/atendimento/{atendimentoId}")
    public List<ExameLaboratorio> listarPorAtendimento(@PathVariable Long atendimentoId) {
        return repository.findByAtendimentoId(atendimentoId);
    }

    @PostMapping
    public ResponseEntity<ExameLaboratorio> criar(@RequestBody ExameLaboratorio exame) {
        if (exame.getAtendimento() == null || exame.getAtendimento().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Long atendimentoId = exame.getAtendimento().getId();

        return atendimentoRepository.findById(atendimentoId).map(atendimento -> {
            exame.setAtendimento(atendimento);
            return ResponseEntity.ok(repository.save(exame));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExameLaboratorio> atualizar(@PathVariable Long id,
            @RequestBody ExameLaboratorio dados) {
        return repository.findById(id).map(exame -> {
            exame.setNome(dados.getNome());
            exame.setResultado(dados.getResultado());
            exame.setDataRealizacao(dados.getDataRealizacao());
            exame.setLaboratorio(dados.getLaboratorio());
            return ResponseEntity.ok(repository.save(exame));
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