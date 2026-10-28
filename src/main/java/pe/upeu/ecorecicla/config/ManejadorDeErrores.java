package pe.upeu.ecorecicla.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pe.upeu.ecorecicla.servicio.ErrorDeNegocio;
import pe.upeu.ecorecicla.web.dto.ErrorApi;

import java.util.LinkedHashMap;
import java.util.Map;

/** Traduce toda excepcion al formato de error comun de la seccion 4.2. */
@RestControllerAdvice
public class ManejadorDeErrores {

	private static final Logger log = LoggerFactory.getLogger(ManejadorDeErrores.class);

	@ExceptionHandler(ErrorDeNegocio.class)
	ResponseEntity<ErrorApi> negocio(ErrorDeNegocio e, HttpServletRequest req) {
		return responder(e.getEstado(), e.getMessage(), req, e.getValidationErrors());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorApi> validacion(MethodArgumentNotValidException e, HttpServletRequest req) {
		Map<String, String> campos = new LinkedHashMap<>();
		e.getBindingResult().getFieldErrors()
				.forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
		return responder(HttpStatus.BAD_REQUEST, "Datos invalidos", req, campos);
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class})
	ResponseEntity<ErrorApi> peticionMalFormada(Exception e, HttpServletRequest req) {
		return responder(HttpStatus.BAD_REQUEST, "La peticion esta mal formada", req, null);
	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ErrorApi> sinPrivilegio(AccessDeniedException e, HttpServletRequest req) {
		return responder(HttpStatus.FORBIDDEN, "No tiene el privilegio necesario para esta operacion", req, null);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	ResponseEntity<ErrorApi> rutaInexistente(NoResourceFoundException e, HttpServletRequest req) {
		return responder(HttpStatus.NOT_FOUND, "El recurso solicitado no existe", req, null);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	ResponseEntity<ErrorApi> metodoNoPermitido(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
		return responder(HttpStatus.METHOD_NOT_ALLOWED, "Metodo HTTP no permitido para esta ruta", req, null);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ErrorApi> inesperado(Exception e, HttpServletRequest req) {
		log.error("Error inesperado en {}", req.getRequestURI(), e);
		return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req, null);
	}

	private ResponseEntity<ErrorApi> responder(HttpStatus estado, String mensaje, HttpServletRequest req,
											   Map<String, String> campos) {
		return ResponseEntity.status(estado).body(ErrorApi.de(estado, mensaje, req.getRequestURI(), campos));
	}
}
