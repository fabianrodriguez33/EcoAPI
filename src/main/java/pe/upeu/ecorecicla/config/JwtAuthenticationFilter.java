package pe.upeu.ecorecicla.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.upeu.ecorecicla.seguridad.UsuarioToken;
import pe.upeu.ecorecicla.web.dto.ErrorApi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Verifica la firma y la vigencia del token en cada peticion. Un token alterado o vencido responde 401;
 * la ausencia de token la resuelve el punto de entrada de SecurityConfig, tambien con 401.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String PREFIJO = "Bearer ";

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String cabecera = request.getHeader("Authorization");
		if (cabecera != null && cabecera.startsWith(PREFIJO)) {
			try {
				UsuarioToken usuario = jwtService.validar(cabecera.substring(PREFIJO.length()).trim());
				List<SimpleGrantedAuthority> autoridades = usuario.privilegios().stream()
						.map(SimpleGrantedAuthority::new).toList();
				SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(usuario, null, autoridades));
			} catch (JwtException | IllegalArgumentException e) {
				SecurityContextHolder.clearContext();
				escribirError(response, request, HttpStatus.UNAUTHORIZED, "Token invalido o vencido");
				return;
			}
		}
		chain.doFilter(request, response);
	}

	static void escribirError(HttpServletResponse response, HttpServletRequest request, HttpStatus estado,
							  String mensaje) throws IOException {
		response.setStatus(estado.value());
		response.setContentType("application/json");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(ErrorApi.de(estado, mensaje, request.getRequestURI(), null).aJson());
	}
}
