package pe.upeu.ecorecicla.seguridad;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.upeu.ecorecicla.config.JwtService;
import pe.upeu.ecorecicla.servicio.ErrorDeNegocio;
import pe.upeu.ecorecicla.web.dto.Dtos.LoginRequest;
import pe.upeu.ecorecicla.web.dto.Dtos.LoginResponse;
import pe.upeu.ecorecicla.web.dto.Dtos.UsuarioActualDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final UsuarioRepository usuarios;
	private final PasswordEncoder encoder;
	private final JwtService jwtService;

	public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwtService) {
		this.usuarios = usuarios;
		this.encoder = encoder;
		this.jwtService = jwtService;
	}

	/** RF-B01: valida contra la base con la contrasena cifrada y devuelve el token firmado. */
	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest peticion) {
		Usuario usuario = usuarios.findByUsername(peticion.usuario().trim())
				.filter(Usuario::isActivo)
				.filter(u -> encoder.matches(peticion.contrasena(), u.getPasswordHash()))
				.orElseThrow(() -> ErrorDeNegocio.noAutenticado("Usuario o contrasena incorrectos"));
		return new LoginResponse(jwtService.generar(usuario), usuario.getUsername(), usuario.getNombre(),
				usuario.getRol().getNombre(), List.copyOf(usuario.codigosDePrivilegios()));
	}

	/** RF-B07: datos y privilegios del usuario del token. */
	@GetMapping("/me")
	public UsuarioActualDto actual(@AuthenticationPrincipal UsuarioToken token) {
		Usuario usuario = usuarios.findById(token.id())
				.orElseThrow(() -> ErrorDeNegocio.noAutenticado("El usuario del token ya no existe"));
		return new UsuarioActualDto(usuario.getId(), usuario.getUsername(), usuario.getNombre(),
				usuario.getRol().getNombre(), List.copyOf(usuario.codigosDePrivilegios()));
	}
}
