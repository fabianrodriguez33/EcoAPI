package pe.upeu.ecorecicla.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/** Peticiones y respuestas del API; los records se agrupan en un solo archivo por brevedad. */
public final class Dtos {

	private Dtos() {
	}

	/** Envoltorio de paginacion de todos los listados (seccion 4.2). */
	public record Pagina<T>(List<T> contenido, int pagina, int tamanio, long totalElementos, int totalPaginas,
							boolean ultima) {
		public static <E, T> Pagina<T> de(Page<E> page, Function<E, T> conversion) {
			return new Pagina<>(page.getContent().stream().map(conversion).toList(), page.getNumber(),
					page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
		}
	}

	public record LoginRequest(
			@NotBlank(message = "El usuario es obligatorio") String usuario,
			@NotBlank(message = "La contrasena es obligatoria") String contrasena) {
	}

	public record LoginResponse(String token, String usuario, String nombre, String rol, List<String> privilegios) {
	}

	public record UsuarioActualDto(Long id, String usuario, String nombre, String rol, List<String> privilegios) {
	}

	public record PuntoDto(Long id, String razonSocial, String direccion, String zona, String materialHabitual) {
	}

	public record PuntoCrearRequest(
			@NotBlank(message = "La razon social es obligatoria") @Size(max = 120, message = "Maximo 120 caracteres") String razonSocial,
			@NotBlank(message = "La direccion es obligatoria") @Size(max = 160, message = "Maximo 160 caracteres") String direccion,
			@NotBlank(message = "La zona es obligatoria") @Size(max = 40, message = "Maximo 40 caracteres") String zona,
			@NotBlank(message = "El material habitual es obligatorio") @Size(max = 60, message = "Maximo 60 caracteres") String materialHabitual) {
	}

	public record PuntoRutaDto(Long id, String razonSocial, String direccion, String zona, String materialHabitual,
							   int secuencia, boolean atendido, String motivoOmision) {
	}

	public record RutaDto(Long id, String fecha, String estado, List<PuntoRutaDto> puntos) {
	}

	public record OmisionRequest(
			@NotBlank(message = "El motivo de omision es obligatorio") @Size(max = 160, message = "Maximo 160 caracteres") String motivo) {
	}

	public record PesajeDto(Long id, Long puntoId, Long rutaId, String fechaHora, String material, double pesoBruto,
							double tara, double pesoNeto, String operario, boolean anulado, String motivoAnulacion,
							String observacion, Double humedadEstimada) {
	}

	public record PesajeCrearRequest(
			@NotNull(message = "La ruta es obligatoria") Long rutaId,
			@NotNull(message = "El punto es obligatorio") Long puntoId,
			@NotBlank(message = "El material es obligatorio") @Size(max = 60, message = "Maximo 60 caracteres") String material,
			@NotNull(message = "El peso bruto es obligatorio") @Positive(message = "El peso bruto debe ser mayor que cero") Double pesoBruto,
			@NotNull(message = "La tara es obligatoria") @PositiveOrZero(message = "La tara no puede ser negativa") Double tara,
			@Size(max = 200, message = "Maximo 200 caracteres") String observacion,
			@DecimalMin(value = "0", message = "La humedad no puede ser negativa") @DecimalMax(value = "100", message = "La humedad no puede superar 100") Double humedadEstimada) {
	}

	public record PesajeEditarRequest(
			@NotBlank(message = "El material es obligatorio") @Size(max = 60, message = "Maximo 60 caracteres") String material,
			@NotNull(message = "El peso bruto es obligatorio") @Positive(message = "El peso bruto debe ser mayor que cero") Double pesoBruto,
			@NotNull(message = "La tara es obligatoria") @PositiveOrZero(message = "La tara no puede ser negativa") Double tara,
			@Size(max = 200, message = "Maximo 200 caracteres") String observacion,
			@DecimalMin(value = "0", message = "La humedad no puede ser negativa") @DecimalMax(value = "100", message = "La humedad no puede superar 100") Double humedadEstimada) {
	}
}
