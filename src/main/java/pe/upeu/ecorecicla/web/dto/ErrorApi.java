package pe.upeu.ecorecicla.web.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/** Formato de error comun de todo el servicio (seccion 4.2). */
public record ErrorApi(String timestamp, int status, String error, String message, String path,
					   Map<String, String> validationErrors) {

	public static ErrorApi de(HttpStatus estado, String mensaje, String path, Map<String, String> validationErrors) {
		String ahora = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
		return new ErrorApi(ahora, estado.value(), estado.getReasonPhrase(), mensaje, path,
				validationErrors == null ? Map.of() : validationErrors);
	}

	/** Serializacion manual para las respuestas escritas desde filtros, donde no hay convertidor de mensajes. */
	public String aJson() {
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"timestamp\":").append(texto(timestamp)).append(',');
		sb.append("\"status\":").append(status).append(',');
		sb.append("\"error\":").append(texto(error)).append(',');
		sb.append("\"message\":").append(texto(message)).append(',');
		sb.append("\"path\":").append(texto(path)).append(',');
		sb.append("\"validationErrors\":{");
		boolean primero = true;
		for (Map.Entry<String, String> e : validationErrors.entrySet()) {
			if (!primero) {
				sb.append(',');
			}
			sb.append(texto(e.getKey())).append(':').append(texto(e.getValue()));
			primero = false;
		}
		return sb.append("}}").toString();
	}

	private static String texto(String valor) {
		if (valor == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder("\"");
		for (char c : valor.toCharArray()) {
			switch (c) {
				case '"' -> sb.append("\\\"");
				case '\\' -> sb.append("\\\\");
				case '\n' -> sb.append("\\n");
				case '\r' -> sb.append("\\r");
				case '\t' -> sb.append("\\t");
				default -> {
					if (c < 0x20) {
						sb.append(String.format("\\u%04x", (int) c));
					} else {
						sb.append(c);
					}
				}
			}
		}
		return sb.append('"').toString();
	}
}
