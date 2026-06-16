package backend.repository;

import backend.entity.ExameLaboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExameLaboratorioRepository extends JpaRepository<ExameLaboratorio, Long> {

    List<ExameLaboratorio> findByAtendimentoId(Long atendimentoId);

    List<ExameLaboratorio> findByNomeContainingIgnoreCase(String nome);
}