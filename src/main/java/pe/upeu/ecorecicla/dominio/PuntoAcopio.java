package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "punto_acopio")
public class PuntoAcopio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String razonSocial;

	@Column(nullable = false)
	private String direccion;

	@Column(nullable = false)
	private String zona;

	@Column(nullable = false)
	private String materialHabitual;

	protected PuntoAcopio() {
	}

	public PuntoAcopio(String razonSocial, String direccion, String zona, String materialHabitual) {
		this.razonSocial = razonSocial;
		this.direccion = direccion;
		this.zona = zona;
		this.materialHabitual = materialHabitual;
	}

	public Long getId() {
		return id;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getZona() {
		return zona;
	}

	public String getMaterialHabitual() {
		return materialHabitual;
	}
}
