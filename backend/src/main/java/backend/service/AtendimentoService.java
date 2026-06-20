package backend.service;

import backend.entity.Atendimento;
import backend.repository.AtendimentoRepository;
import backend.repository.ProfissionalSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final AtendimentoRepository repository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public List<Atendimento> listarTodos() {
        return repository.findAll();
    }

    public Optional<Atendimento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Atendimento> listarPorProfissional(Long profissionalId) {
        return repository.findByProfissionalSaudeId(profissionalId);
    }

    public Optional<Atendimento> criar(Atendimento atendimento) {
        if (atendimento.getProfissionalSaude() == null || atendimento.getProfissionalSaude().getId() == null) {
            return Optional.empty();
        }
        Long profissionalId = atendimento.getProfissionalSaude().getId();

        return profissionalRepository.findById(profissionalId).map(profissional -> {
            atendimento.setProfissionalSaude(profissional);
            if (atendimento.getExamesLaboratorio() != null) {
                atendimento.getExamesLaboratorio().forEach(exame -> exame.setAtendimento(atendimento));
            }
            return repository.save(atendimento);
        });
    }

    public Optional<Atendimento> atualizar(Long id, Atendimento dados) {
        return repository.findById(id).map(atendimento -> {
            atendimento.setDataHora(dados.getDataHora());
            atendimento.setDescricao(dados.getDescricao());
            atendimento.setObservacoes(dados.getObservacoes());
            return repository.save(atendimento);
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
