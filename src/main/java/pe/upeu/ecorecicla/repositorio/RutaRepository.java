package pe.upeu.ecorecicla.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.upeu.ecorecicla.dominio.Ruta;

import java.time.LocalDate;
import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

	@Query("select r from Ruta r where r.fecha = :fecha and (r.operario.id = :usuarioId or r.supervisor.id = :usuarioId) order by r.id")
	List<Ruta> buscarDelUsuario(@Param("fecha") LocalDate fecha, @Param("usuarioId") Long usuarioId);
}
