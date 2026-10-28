package pe.upeu.ecorecicla.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.upeu.ecorecicla.seguridad.Usuario;
import pe.upeu.ecorecicla.seguridad.UsuarioToken;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class JwtService {

	private final SecretKey clave;
	private final long vigenciaSegundos;

	public JwtService(@Value("${eco.jwt.secreto}") String secreto,
					  @Value("${eco.jwt.vigencia-segundos:3600}") long vigenciaSegundos) {
		this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
		this.vigenciaSegundos = vigenciaSegundos;
	}

	public String generar(Usuario usuario) {
		long ahora = System.currentTimeMillis();
		return Jwts.builder()
				.subject(String.valueOf(usuario.getId()))
				.claim("usuario", usuario.getUsername())
				.claim("rol", usuario.getRol().getNombre())
				.claim("privilegios", List.copyOf(usuario.codigosDePrivilegios()))
				.issuedAt(new Date(ahora))
				.expiration(new Date(ahora + vigenciaSegundos * 1000))
				.signWith(clave)
				.compact();
	}

	/** Verifica firma y vigencia; lanza JwtException si el token esta alterado o vencido. */
	public UsuarioToken validar(String token) {
		Claims claims = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
		Set<String> privilegios = new LinkedHashSet<>();
		Object lista = claims.get("privilegios");
		if (lista instanceof List<?> items) {
			items.forEach(item -> privilegios.add(String.valueOf(item)));
		}
		try {
			return new UsuarioToken(Long.valueOf(claims.getSubject()), claims.get("usuario", String.class),
					claims.get("rol", String.class), privilegios);
		} catch (NumberFormatException e) {
			throw new JwtException("Token con identificador invalido");
		}
	}

	public long getVigenciaSegundos() {
		return vigenciaSegundos;
	}
}
