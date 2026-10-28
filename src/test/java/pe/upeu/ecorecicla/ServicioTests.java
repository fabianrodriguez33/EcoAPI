package pe.upeu.ecorecicla;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import pe.upeu.ecorecicla.config.JwtService;
import pe.upeu.ecorecicla.seguridad.Privilegio;
import pe.upeu.ecorecicla.seguridad.Rol;
import pe.upeu.ecorecicla.seguridad.Usuario;
import pe.upeu.ecorecicla.seguridad.UsuarioToken;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioTests {

	private static final String SECRETO = "una-clave-de-prueba-con-mas-de-treinta-y-dos-bytes-0123456789";

	private Usuario usuario() {
		Rol rol = new Rol("OPERARIO", Set.of(new Privilegio("PUNTO_LEER", "x"), new Privilegio("PESAJE_CREAR", "y")));
		Usuario usuario = new Usuario("operario1", "hash", "Operario Uno", rol);
		ReflectionTestUtils.setField(usuario, "id", 7L);
		return usuario;
	}

	@Test
	void tokenGeneradoSeValidaYTransportaPrivilegios() {
		JwtService jwt = new JwtService(SECRETO, 3600);
		UsuarioToken token = jwt.validar(jwt.generar(usuario()));
		assertEquals("operario1", token.usuario());
		assertEquals(7L, token.id());
		assertTrue(token.tiene("PESAJE_CREAR"));
	}

	@Test
	void tokenAlteradoOVencidoSeRechaza() {
		JwtService jwt = new JwtService(SECRETO, 3600);
		String valido = jwt.generar(usuario());
		int medio = valido.indexOf('.') + 10;
		char original = valido.charAt(medio);
		String alterado = valido.substring(0, medio) + (original == 'A' ? 'B' : 'A') + valido.substring(medio + 1);
		assertThrows(JwtException.class, () -> jwt.validar(alterado));
		JwtService vencido = new JwtService(SECRETO, -10);
		assertThrows(JwtException.class, () -> jwt.validar(vencido.generar(usuario())));
	}
}
