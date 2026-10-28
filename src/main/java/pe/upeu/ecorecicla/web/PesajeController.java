package pe.upeu.ecorecicla.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.upeu.ecorecicla.seguridad.UsuarioToken;
import pe.upeu.ecorecicla.servicio.PesajeService;
import pe.upeu.ecorecicla.web.dto.Dtos.PesajeCrearRequest;
import pe.upeu.ecorecicla.web.dto.Dtos.PesajeDto;
import pe.upeu.ecorecicla.web.dto.Dtos.PesajeEditarRequest;

import java.net.URI;

/** El controlador solo exige el privilegio; las reglas RN-01 a RN-03 viven en PesajeService. */
@RestController
@RequestMapping("/api/v1/pesajes")
public class PesajeController {

	private final PesajeService pesajes;

	public PesajeController(PesajeService pesajes) {
		this.pesajes = pesajes;
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PESAJE_CREAR')")
	public ResponseEntity<PesajeDto> crear(@AuthenticationPrincipal UsuarioToken actual,
										   @Valid @RequestBody PesajeCrearRequest peticion) {
		PesajeDto creado = pesajes.crear(actual, peticion);
		return ResponseEntity.created(URI.create("/api/v1/pesajes/" + creado.id())).body(creado);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('PESAJE_EDITAR')")
	public PesajeDto corregir(@AuthenticationPrincipal UsuarioToken actual, @PathVariable Long id,
							  @Valid @RequestBody PesajeEditarRequest peticion) {
		return pesajes.corregir(actual, id, peticion);
	}

	/** La anulacion no borra el registro: lo marca con el motivo. */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('PESAJE_ANULAR')")
	public ResponseEntity<Void> anular(@PathVariable Long id, @RequestParam(required = false) String motivo) {
		pesajes.anular(id, motivo);
		return ResponseEntity.noContent().build();
	}
}
