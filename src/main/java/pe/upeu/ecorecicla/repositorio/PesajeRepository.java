package pe.upeu.ecorecicla.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.upeu.ecorecicla.dominio.Pesaje;

import java.util.Set;

public interface PesajeRepository extends JpaRepository<Pesaje, Long> {

	Page<Pesaje> findByPuntoId(Long puntoId, Pageable pageable);

	@Query("select distinct p.punto.id from Pesaje p where p.ruta.id = :rutaId and p.anulado = false")
	Set<Long> puntosConPesaje(@Param("rutaId") Long rutaId);
}
