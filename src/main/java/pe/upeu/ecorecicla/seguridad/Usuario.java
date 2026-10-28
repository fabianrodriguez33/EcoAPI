package pe.upeu.ecorecicla.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private Rol rol;

	private boolean activo = true;

	protected Usuario() {
	}

	public Usuario(String username, String passwordHash, String nombre, Rol rol) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.nombre = nombre;
		this.rol = rol;
	}

	public Set<String> codigosDePrivilegios() {
		Set<String> codigos = new TreeSet<>();
		rol.getPrivilegios().forEach(p -> codigos.add(p.getCodigo()));
		return codigos;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getNombre() {
		return nombre;
	}

	public Rol getRol() {
		return rol;
	}

	public boolean isActivo() {
		return activo;
	}
}
