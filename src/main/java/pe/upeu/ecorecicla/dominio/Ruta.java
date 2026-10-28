package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import pe.upeu.ecorecicla.seguridad.Usuario;

import java.time.LocalDate;

@Entity
@Table(name = "ruta")
public class Ruta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate fecha;

	@ManyToOne(optional = false)
	private Usuario operario;

	@ManyToOne(optional = false)
	private Usuario supervisor;

	private boolean cerrada;

	protected Ruta() {
	}

	public Ruta(LocalDate fecha, Usuario operario, Usuario supervisor, boolean cerrada) {
		this.fecha = fecha;
		this.operario = operario;
		this.supervisor = supervisor;
		this.cerrada = cerrada;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public Usuario getOperario() {
		return operario;
	}

	public Usuario getSupervisor() {
		return supervisor;
	}

	public boolean isCerrada() {
		return cerrada;
	}

	public void cerrar() {
		this.cerrada = true;
	}
}
