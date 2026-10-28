package pe.upeu.ecorecicla.servicio;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upeu.ecorecicla.dominio.Pesaje;
import pe.upeu.ecorecicla.dominio.PuntoAcopio;
import pe.upeu.ecorecicla.dominio.Ruta;
import pe.upeu.ecorecicla.repositorio.PesajeRepository;
import pe.upeu.ecorecicla.repositorio.PuntoAcopioRepository;
import pe.upeu.ecorecicla.repositorio.RutaPuntoRepository;
import pe.upeu.ecorecicla.repositorio.RutaRepository;
import pe.upeu.ecorecicla.seguridad.Usuario;
import pe.upeu.ecorecicla.seguridad.UsuarioRepository;
import pe.upeu.ecorecicla.seguridad.UsuarioToken;
import pe.upeu.ecorecicla.web.dto.Dtos.Pagina;
import pe.upeu.ecorecicla.web.dto.Dtos.PesajeCrearRequest;
import pe.upeu.ecorecicla.web.dto.Dtos.PesajeDto;
import pe.upeu.ecorecicla.web.dto.Dtos.PesajeEditarRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/** Aplica las reglas RN-01, RN-02 y RN-03 sobre los pesajes. */
@Service
public class PesajeService {

	private final PesajeRepository pesajes;
	private final PuntoAcopioRepository puntos;
	private final RutaRepository rutas;
	private final RutaPuntoRepository rutaPuntos;
	private final UsuarioRepository usuarios;

	public PesajeService(PesajeRepository pesajes, PuntoAcopioRepository puntos, RutaRepository rutas,
						 RutaPuntoRepository rutaPuntos, UsuarioRepository usuarios) {
		this.pesajes = pesajes;
		this.puntos = puntos;
		this.rutas = rutas;
		this.rutaPuntos = rutaPuntos;
		this.usuarios = usuarios;
	}

	@Transactional(readOnly = true)
	public Pagina<PesajeDto> listarPorPunto(Long puntoId, int pagina, int tamanio) {
		if (!puntos.existsById(puntoId)) {
			throw ErrorDeNegocio.noEncontrado("El punto de acopio no existe");
		}
		PageRequest pedido = PageRequest.of(pagina, tamanio, Sort.by(Sort.Direction.DESC, "fechaHora", "id"));
		return Pagina.de(pesajes.findByPuntoId(puntoId, pedido), PesajeService::aDto);
	}

	@Transactional
	public PesajeDto crear(UsuarioToken actual, PesajeCrearRequest peticion) {
		Ruta ruta = rutas.findById(peticion.rutaId())
				.orElseThrow(() -> ErrorDeNegocio.noEncontrado("La ruta no existe"));
		PuntoAcopio punto = puntos.findById(peticion.puntoId())
				.orElseThrow(() -> ErrorDeNegocio.noEncontrado("El punto de acopio no existe"));
		if (rutaPuntos.findByRutaIdAndPuntoId(ruta.getId(), punto.getId()).isEmpty()) {
			throw ErrorDeNegocio.noEncontrado("El punto no pertenece a la ruta indicada");
		}
		if (ruta.isCerrada()) {
			throw ErrorDeNegocio.conflicto("La ruta ya esta cerrada: no se pueden registrar pesajes");
		}
		double neto = calcularNeto(peticion.pesoBruto(), peticion.tara());
		Usuario operario = usuarios.findById(actual.id())
				.orElseThrow(() -> ErrorDeNegocio.noAutenticado("El usuario del token ya no existe"));
		Pesaje pesaje = new Pesaje(ruta, punto, operario, ahora(), peticion.material().trim(),
				peticion.pesoBruto(), peticion.tara(), neto, limpiar(peticion.observacion()));
		return aDto(pesajes.save(pesaje));
	}

	@Transactional
	public PesajeDto corregir(UsuarioToken actual, Long id, PesajeEditarRequest peticion) {
		Pesaje pesaje = buscar(id);
		if (pesaje.getRuta().isCerrada()) {
			throw ErrorDeNegocio.conflicto("La ruta ya esta cerrada: no se pueden corregir pesajes");
		}
		if (pesaje.isAnulado()) {
			throw ErrorDeNegocio.conflicto("El pesaje esta anulado y no se puede corregir");
		}
		if (!actual.tiene(UsuarioToken.PESAJE_EDITAR_AJENOS)) {
			if (!pesaje.getOperario().getId().equals(actual.id())) {
				throw ErrorDeNegocio.prohibido("Solo puede corregir los pesajes que usted registro");
			}
			if (!pesaje.getFechaHora().toLocalDate().equals(LocalDate.now())) {
				throw ErrorDeNegocio.prohibido("Solo puede corregir un pesaje dentro del mismo dia de su registro");
			}
		}
		double neto = calcularNeto(peticion.pesoBruto(), peticion.tara());
		pesaje.corregir(peticion.material().trim(), peticion.pesoBruto(), peticion.tara(), neto,
				limpiar(peticion.observacion()));
		return aDto(pesaje);
	}

	@Transactional
	public void anular(Long id, String motivo) {
		Pesaje pesaje = buscar(id);
		if (pesaje.isAnulado()) {
			throw ErrorDeNegocio.conflicto("El pesaje ya esta anulado");
		}
		String razon = motivo == null || motivo.isBlank() ? "Anulado por el supervisor" : motivo.trim();
		pesaje.anular(razon);
	}

	private Pesaje buscar(Long id) {
		return pesajes.findById(id).orElseThrow(() -> ErrorDeNegocio.noEncontrado("El pesaje no existe"));
	}

	/** RN-03: el neto lo calcula el servicio y debe ser mayor que cero. */
	static double calcularNeto(double bruto, double tara) {
		double neto = Math.round((bruto - tara) * 100.0) / 100.0;
		if (neto <= 0) {
			throw ErrorDeNegocio.validacion("pesoBruto", "El peso bruto debe ser mayor que la tara");
		}
		return neto;
	}

	private static LocalDateTime ahora() {
		return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	private static String limpiar(String texto) {
		return texto == null || texto.isBlank() ? null : texto.trim();
	}

	static PesajeDto aDto(Pesaje p) {
		return new PesajeDto(p.getId(), p.getPunto().getId(), p.getRuta().getId(),
				DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(p.getFechaHora()), p.getMaterial(), p.getPesoBruto(),
				p.getTara(), p.getPesoNeto(), p.getOperario().getUsername(), p.isAnulado(),
				p.getMotivoAnulacion(), p.getObservacion());
	}
}
