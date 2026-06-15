package backend.repository;

import backend.entity.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository
        extends JpaRepository<ProfissionalSaude, Long> {
}