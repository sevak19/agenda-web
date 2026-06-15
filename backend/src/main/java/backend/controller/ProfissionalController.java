package backend.controller;

import backend.entity.ProfissionalSaude;
import backend.repository.ProfissionalRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
@CrossOrigin("*")
public class ProfissionalController {

    private final ProfissionalRepository repository;

    public ProfissionalController(
            ProfissionalRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ProfissionalSaude> listar() {
        return repository.findAll();
    }

    @PostMapping
    public ProfissionalSaude salvar(
            @RequestBody ProfissionalSaude profissional) {

        return repository.save(profissional);
    }
}