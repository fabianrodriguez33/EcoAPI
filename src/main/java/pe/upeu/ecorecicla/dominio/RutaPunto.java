package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Punto planificado dentro de una ruta, con su orden de visita y, si se omitio, el motivo. */
@Entity
@Table(name = "ruta_punto", uniqueConstraints = @UniqueConstraint(columnNames = {"ruta_id", "punto_id"}))
public class RutaPunto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Ruta ruta;

	@ManyToOne(optional = false)
	private PuntoAcopio punto;

	private int secuencia;

	private String motivoOmision;

	protected RutaPunto() {
	}

	public RutaPunto(Ruta ruta, PuntoAcopio punto, int secuencia) {
		this.ruta = ruta;
		this.punto = punto;
		this.secuencia = secuencia;
	}

	public Long getId() {
		return id;
	}

	public Ruta getRuta() {
		return ruta;
	}

	public PuntoAcopio getPunto() {
		return punto;
	}

	public int getSecuencia() {
		return secuencia;
	}

	public String getMotivoOmision() {
		return motivoOmision;
	}

	public void omitir(String motivo) {
		this.motivoOmision = motivo;
	}
}
