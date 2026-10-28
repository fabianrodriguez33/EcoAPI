package pe.upeu.ecorecicla.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rol")
public class Rol {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String nombre;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "rol_privilegio",
			joinColumns = @JoinColumn(name = "rol_id"),
			inverseJoinColumns = @JoinColumn(name = "privilegio_codigo"))
	private Set<Privilegio> privilegios = new HashSet<>();

	protected Rol() {
	}

	public Rol(String nombre, Set<Privilegio> privilegios) {
		this.nombre = nombre;
		this.privilegios = privilegios;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public Set<Privilegio> getPrivilegios() {
		return privilegios;
	}
}
