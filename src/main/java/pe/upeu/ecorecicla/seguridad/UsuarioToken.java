package pe.upeu.ecorecicla.seguridad;

import java.util.Set;

/** Identidad extraida de un token verificado: es el principal de la autenticacion. */
public record UsuarioToken(Long id, String usuario, String rol, Set<String> privilegios) {

	/** Privilegio que distingue a quien puede corregir pesajes de otros operarios (RN-02). */
	public static final String PESAJE_EDITAR_AJENOS = "PESAJE_EDITAR_AJENOS";

	public boolean tiene(String privilegio) {
		return privilegios.contains(privilegio);
	}
}
