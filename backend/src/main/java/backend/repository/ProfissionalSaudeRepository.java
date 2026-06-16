package backend.repository;

import backend.entity.Categoria;
import backend.entity.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfissionalSaudeRepository
        extends JpaRepository<ProfissionalSaude, Long> {
                List<ProfissionalSaude> findByNomeContainingIgnoreCase(String nome);
                List<ProfissionalSaude> findByCategoria(Categoria categoria);
}