package pe.upeu.ecorecicla.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.upeu.ecorecicla.dominio.RutaPunto;

import java.util.List;
import java.util.Optional;

public interface RutaPuntoRepository extends JpaRepository<RutaPunto, Long> {

	List<RutaPunto> findByRutaIdOrderBySecuencia(Long rutaId);

	Optional<RutaPunto> findByRutaIdAndPuntoId(Long rutaId, Long puntoId);
}
