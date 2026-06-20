package backend.service;

import backend.entity.ProfissionalSaude;
import backend.repository.ProfissionalSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfissionalSaudeService {

    private final ProfissionalSaudeRepository repository;

    public List<ProfissionalSaude> listarTodos() {
        return repository.findAll();
    }

    public Optional<ProfissionalSaude> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<ProfissionalSaude> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public ProfissionalSaude criar(ProfissionalSaude profissional) {
        return repository.save(profissional);
    }

    public Optional<ProfissionalSaude> atualizar(Long id, ProfissionalSaude dados) {
        return repository.findById(id).map(profissional -> {
            profissional.setNome(dados.getNome());
            profissional.setTelefone(dados.getTelefone());
            profissional.setEndereco(dados.getEndereco());
            profissional.setCategoria(dados.getCategoria());
            return repository.save(profissional);
        });
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
