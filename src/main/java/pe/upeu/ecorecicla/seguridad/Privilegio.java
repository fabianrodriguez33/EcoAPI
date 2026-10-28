package pe.upeu.ecorecicla.seguridad;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "privilegio")
public class Privilegio {

	@Id
	private String codigo;
	private String descripcion;

	protected Privilegio() {
	}

	public Privilegio(String codigo, String descripcion) {
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
