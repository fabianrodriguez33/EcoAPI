package pe.upeu.ecorecicla.web;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.upeu.ecorecicla.seguridad.UsuarioToken;
import pe.upeu.ecorecicla.servicio.RutaService;
import pe.upeu.ecorecicla.web.dto.Dtos.OmisionRequest;
import pe.upeu.ecorecicla.web.dto.Dtos.RutaDto;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/rutas")
public class RutaController {

	private final RutaService rutas;

	public RutaController(RutaService rutas) {
		this.rutas = rutas;
	}

	/** RF-B02: ruta del usuario autenticado para una fecha (hoy por omision). */
	@GetMapping("/dia")
	@PreAuthorize("hasAuthority('PUNTO_LEER')")
	public RutaDto delDia(@AuthenticationPrincipal UsuarioToken actual,
						  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
		return rutas.delDia(actual, fecha == null ? LocalDate.now() : fecha);
	}

	/** RF-B06: cierre de la ruta; la regla RN-04 la aplica RutaService. */
	@PostMapping("/{id}/cerrar")
	@PreAuthorize("hasAuthority('RUTA_CERRAR')")
	public RutaDto cerrar(@PathVariable Long id) {
		return rutas.cerrar(id);
	}

	/** Registra el motivo por el que un punto planificado no se pesa. */
	@PostMapping("/{id}/puntos/{puntoId}/omision")
	@PreAuthorize("hasAuthority('PESAJE_CREAR')")
	public RutaDto omitir(@PathVariable Long id, @PathVariable Long puntoId,
						  @Valid @RequestBody OmisionRequest peticion) {
		return rutas.omitirPunto(id, puntoId, peticion.motivo());
	}
}
