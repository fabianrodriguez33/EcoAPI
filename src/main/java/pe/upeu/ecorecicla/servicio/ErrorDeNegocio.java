package pe.upeu.ecorecicla.servicio;

import org.springframework.http.HttpStatus;

import java.util.Map;

/** Error que la capa de servicio lanza al incumplirse una regla; ManejadorDeErrores lo traduce a HTTP. */
public class ErrorDeNegocio extends RuntimeException {

	private final HttpStatus estado;
	private final Map<String, String> validationErrors;

	private ErrorDeNegocio(HttpStatus estado, String mensaje, Map<String, String> validationErrors) {
		super(mensaje);
		this.estado = estado;
		this.validationErrors = validationErrors;
	}

	public static ErrorDeNegocio validacion(String campo, String mensaje) {
		return new ErrorDeNegocio(HttpStatus.BAD_REQUEST, "Datos invalidos", Map.of(campo, mensaje));
	}

	public static ErrorDeNegocio noAutenticado(String mensaje) {
		return new ErrorDeNegocio(HttpStatus.UNAUTHORIZED, mensaje, Map.of());
	}

	public static ErrorDeNegocio prohibido(String mensaje) {
		return new ErrorDeNegocio(HttpStatus.FORBIDDEN, mensaje, Map.of());
	}

	public static ErrorDeNegocio noEncontrado(String mensaje) {
		return new ErrorDeNegocio(HttpStatus.NOT_FOUND, mensaje, Map.of());
	}

	public static ErrorDeNegocio conflicto(String mensaje) {
		return new ErrorDeNegocio(HttpStatus.CONFLICT, mensaje, Map.of());
	}

	public HttpStatus getEstado() {
		return estado;
	}

	public Map<String, String> getValidationErrors() {
		return validationErrors;
	}
}
