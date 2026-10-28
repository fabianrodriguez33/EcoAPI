package pe.upeu.ecorecicla.servicio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upeu.ecorecicla.dominio.PuntoAcopio;
import pe.upeu.ecorecicla.repositorio.PuntoAcopioRepository;
import pe.upeu.ecorecicla.web.dto.Dtos.Pagina;
import pe.upeu.ecorecicla.web.dto.Dtos.PuntoCrearRequest;
import pe.upeu.ecorecicla.web.dto.Dtos.PuntoDto;

@Service
public class PuntoService {

	private final PuntoAcopioRepository puntos;

	public PuntoService(PuntoAcopioRepository puntos) {
		this.puntos = puntos;
	}

	@Transactional(readOnly = true)
	public Pagina<PuntoDto> listar(String zona, int pagina, int tamanio) {
		PageRequest pedido = PageRequest.of(pagina, tamanio, Sort.by("zona", "razonSocial"));
		Page<PuntoAcopio> resultado = zona == null || zona.isBlank()
				? puntos.findAll(pedido)
				: puntos.findByZonaIgnoreCase(zona.trim(), pedido);
		return Pagina.de(resultado, PuntoService::aDto);
	}

	@Transactional
	public PuntoDto crear(PuntoCrearRequest p) {
		return aDto(puntos.save(new PuntoAcopio(p.razonSocial().trim(), p.direccion().trim(), p.zona().trim(),
				p.materialHabitual().trim())));
	}

	private static PuntoDto aDto(PuntoAcopio p) {
		return new PuntoDto(p.getId(), p.getRazonSocial(), p.getDireccion(), p.getZona(), p.getMaterialHabitual());
	}
}
