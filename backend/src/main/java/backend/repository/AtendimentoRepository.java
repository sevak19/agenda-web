package backend.repository;

import backend.entity.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByProfissionalSaudeId(Long profissionalSaudeId);

    List<Atendimento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
}