package backend.service;

import backend.entity.ExameLaboratorio;
import backend.repository.AtendimentoRepository;
import backend.repository.ExameLaboratorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExameLaboratorioService {

    private final ExameLaboratorioRepository repository;
    private final AtendimentoRepository atendimentoRepository;

    public List<ExameLaboratorio> listarTodos() {
        return repository.findAll();
    }

    public Optional<ExameLaboratorio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<ExameLaboratorio> listarPorAtendimento(Long atendimentoId) {
        return repository.findByAtendimentoId(atendimentoId);
    }

    public Optional<ExameLaboratorio> criar(ExameLaboratorio exame) {
        if (exame.getAtendimento() == null || exame.getAtendimento().getId() == null) {
            return Optional.empty();
        }
        Long atendimentoId = exame.getAtendimento().getId();

        return atendimentoRepository.findById(atendimentoId).map(atendimento -> {
            exame.setAtendimento(atendimento);
            return repository.save(exame);
        });
    }

    public Optional<ExameLaboratorio> atualizar(Long id, ExameLaboratorio dados) {
        return repository.findById(id).map(exame -> {
            exame.setNome(dados.getNome());
            exame.setResultado(dados.getResultado());
            exame.setDataRealizacao(dados.getDataRealizacao());
            exame.setLaboratorio(dados.getLaboratorio());
            return repository.save(exame);
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
