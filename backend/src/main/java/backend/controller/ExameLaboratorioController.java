package backend.controller;

import backend.entity.ExameLaboratorio;
import backend.service.ExameLaboratorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exames")
@RequiredArgsConstructor
public class ExameLaboratorioController {

    private final ExameLaboratorioService service;

    @GetMapping
    public List<ExameLaboratorio> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameLaboratorio> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/atendimento/{atendimentoId}")
    public List<ExameLaboratorio> listarPorAtendimento(@PathVariable Long atendimentoId) {
        return service.listarPorAtendimento(atendimentoId);
    }

    @PostMapping
    public ResponseEntity<ExameLaboratorio> criar(@RequestBody ExameLaboratorio exame) {
        return service.criar(exame)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExameLaboratorio> atualizar(@PathVariable Long id,
            @RequestBody ExameLaboratorio dados) {
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
