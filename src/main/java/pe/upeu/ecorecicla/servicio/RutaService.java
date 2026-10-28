package pe.upeu.ecorecicla.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upeu.ecorecicla.dominio.Ruta;
import pe.upeu.ecorecicla.dominio.RutaPunto;
import pe.upeu.ecorecicla.repositorio.PesajeRepository;
import pe.upeu.ecorecicla.repositorio.RutaPuntoRepository;
import pe.upeu.ecorecicla.repositorio.RutaRepository;
import pe.upeu.ecorecicla.seguridad.UsuarioToken;
import pe.upeu.ecorecicla.web.dto.Dtos.PuntoRutaDto;
import pe.upeu.ecorecicla.web.dto.Dtos.RutaDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/** Consulta la ruta del dia y aplica RN-01 y RN-04. */
@Service
public class RutaService {

	private final RutaRepository rutas;
	private final RutaPuntoRepository rutaPuntos;
	private final PesajeRepository pesajes;

	public RutaService(RutaRepository rutas, RutaPuntoRepository rutaPuntos, PesajeRepository pesajes) {
		this.rutas = rutas;
		this.rutaPuntos = rutaPuntos;
		this.pesajes = pesajes;
	}

	@Transactional(readOnly = true)
	public RutaDto delDia(UsuarioToken actual, LocalDate fecha) {
		List<Ruta> candidatas = rutas.buscarDelUsuario(fecha, actual.id());
		if (candidatas.isEmpty()) {
			throw ErrorDeNegocio.noEncontrado("No hay una ruta asignada para la fecha " + fecha);
		}
		return aDto(candidatas.get(0));
	}

	/** RN-04: no se cierra mientras algun punto planificado no tenga pesaje ni motivo de omision. */
	@Transactional
	public RutaDto cerrar(Long rutaId) {
		Ruta ruta = buscar(rutaId);
		if (ruta.isCerrada()) {
			throw ErrorDeNegocio.conflicto("La ruta ya esta cerrada");
		}
		Set<Long> pesados = pesajes.puntosConPesaje(ruta.getId());
		long faltan = rutaPuntos.findByRutaIdOrderBySecuencia(ruta.getId()).stream()
				.filter(rp -> !atendido(rp, pesados)).count();
		if (faltan > 0) {
			throw ErrorDeNegocio.conflicto("No se puede cerrar la ruta: faltan " + faltan
					+ (faltan == 1 ? " punto sin pesaje ni motivo de omision"
					: " puntos sin pesaje ni motivo de omision"));
		}
		ruta.cerrar();
		return aDto(ruta);
	}

	@Transactional
	public RutaDto omitirPunto(Long rutaId, Long puntoId, String motivo) {
		Ruta ruta = buscar(rutaId);
		RutaPunto rutaPunto = rutaPuntos.findByRutaIdAndPuntoId(rutaId, puntoId)
				.orElseThrow(() -> ErrorDeNegocio.noEncontrado("El punto no pertenece a la ruta indicada"));
		if (ruta.isCerrada()) {
			throw ErrorDeNegocio.conflicto("La ruta ya esta cerrada: no se pueden registrar omisiones");
		}
		rutaPunto.omitir(motivo.trim());
		return aDto(ruta);
	}

	private Ruta buscar(Long id) {
		return rutas.findById(id).orElseThrow(() -> ErrorDeNegocio.noEncontrado("La ruta no existe"));
	}

	private static boolean atendido(RutaPunto rp, Set<Long> pesados) {
		return pesados.contains(rp.getPunto().getId()) || rp.getMotivoOmision() != null;
	}

	private RutaDto aDto(Ruta ruta) {
		Set<Long> pesados = pesajes.puntosConPesaje(ruta.getId());
		List<PuntoRutaDto> puntos = rutaPuntos.findByRutaIdOrderBySecuencia(ruta.getId()).stream()
				.map(rp -> new PuntoRutaDto(rp.getPunto().getId(), rp.getPunto().getRazonSocial(),
						rp.getPunto().getDireccion(), rp.getPunto().getZona(), rp.getPunto().getMaterialHabitual(),
						rp.getSecuencia(), atendido(rp, pesados), rp.getMotivoOmision()))
				.toList();
		return new RutaDto(ruta.getId(), ruta.getFecha().toString(), ruta.isCerrada() ? "CERRADA" : "ABIERTA", puntos);
	}
}
