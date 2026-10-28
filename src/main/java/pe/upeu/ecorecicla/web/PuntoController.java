package pe.upeu.ecorecicla.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.upeu.ecorecicla.servicio.PesajeService;
import pe.upeu.ecorecicla.servicio.PuntoService;
import pe.upeu.ecorecicla.web.dto.Dtos.Pagina;
import pe.upeu.ecorecicla.web.dto.Dtos.PesajeDto;
import pe.upeu.ecorecicla.web.dto.Dtos.PuntoCrearRequest;
import pe.upeu.ecorecicla.web.dto.Dtos.PuntoDto;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/puntos")
public class PuntoController {

	private final PuntoService puntos;
	private final PesajeService pesajes;

	public PuntoController(PuntoService puntos, PesajeService pesajes) {
		this.puntos = puntos;
		this.pesajes = pesajes;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('PUNTO_LEER')")
	public Pagina<PuntoDto> listar(@RequestParam(required = false) String zona,
								   @RequestParam(defaultValue = "0") int pagina,
								   @RequestParam(defaultValue = "20") int tamanio) {
		return puntos.listar(zona, Paginacion.pagina(pagina), Paginacion.tamanio(tamanio));
	}

	@PostMapping
	@PreAuthorize("hasAuthority('PUNTO_GESTIONAR')")
	public ResponseEntity<PuntoDto> crear(@Valid @RequestBody PuntoCrearRequest peticion) {
		PuntoDto creado = puntos.crear(peticion);
		return ResponseEntity.created(URI.create("/api/v1/puntos/" + creado.id())).body(creado);
	}

	@GetMapping("/{id}/pesajes")
	@PreAuthorize("hasAuthority('PESAJE_LEER')")
	public Pagina<PesajeDto> pesajes(@PathVariable Long id,
									 @RequestParam(defaultValue = "0") int pagina,
									 @RequestParam(defaultValue = "20") int tamanio) {
		return pesajes.listarPorPunto(id, Paginacion.pagina(pagina), Paginacion.tamanio(tamanio));
	}
}
