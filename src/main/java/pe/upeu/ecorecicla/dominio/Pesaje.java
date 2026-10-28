package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import pe.upeu.ecorecicla.seguridad.Usuario;

import java.time.LocalDateTime;

@Entity
@Table(name = "pesaje")
public class Pesaje {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Ruta ruta;

	@ManyToOne(optional = false)
	private PuntoAcopio punto;

	@ManyToOne(optional = false)
	private Usuario operario;

	@Column(nullable = false)
	private LocalDateTime fechaHora;

	@Column(nullable = false)
	private String material;

	private double pesoBruto;
	private double tara;
	private double pesoNeto;

	private String observacion;

	/** Humedad estimada del material en porcentaje (0 a 100); opcional. */
	private Double humedadEstimada;

	private boolean anulado;
	private String motivoAnulacion;

	protected Pesaje() {
	}

	public Pesaje(Ruta ruta, PuntoAcopio punto, Usuario operario, LocalDateTime fechaHora, String material,
				  double pesoBruto, double tara, double pesoNeto, String observacion) {
		this.ruta = ruta;
		this.punto = punto;
		this.operario = operario;
		this.fechaHora = fechaHora;
		this.material = material;
		this.pesoBruto = pesoBruto;
		this.tara = tara;
		this.pesoNeto = pesoNeto;
		this.observacion = observacion;
	}

	public Pesaje(Ruta ruta, PuntoAcopio punto, Usuario operario, LocalDateTime fechaHora, String material,
				  double pesoBruto, double tara, double pesoNeto, String observacion, Double humedadEstimada) {
		this(ruta, punto, operario, fechaHora, material, pesoBruto, tara, pesoNeto, observacion);
		this.humedadEstimada = humedadEstimada;
	}

	public void corregir(String material, double pesoBruto, double tara, double pesoNeto, String observacion,
						 Double humedadEstimada) {
		this.humedadEstimada = humedadEstimada;
		corregir(material, pesoBruto, tara, pesoNeto, observacion);
	}

	public void corregir(String material, double pesoBruto, double tara, double pesoNeto, String observacion) {
		this.material = material;
		this.pesoBruto = pesoBruto;
		this.tara = tara;
		this.pesoNeto = pesoNeto;
		this.observacion = observacion;
	}

	public void anular(String motivo) {
		this.anulado = true;
		this.motivoAnulacion = motivo;
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

	public Usuario getOperario() {
		return operario;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public String getMaterial() {
		return material;
	}

	public double getPesoBruto() {
		return pesoBruto;
	}

	public double getTara() {
		return tara;
	}

	public double getPesoNeto() {
		return pesoNeto;
	}

	public String getObservacion() {
		return observacion;
	}

	public Double getHumedadEstimada() {
		return humedadEstimada;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public String getMotivoAnulacion() {
		return motivoAnulacion;
	}
}
