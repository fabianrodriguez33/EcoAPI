package pe.upeu.ecorecicla.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.upeu.ecorecicla.dominio.Pesaje;
import pe.upeu.ecorecicla.dominio.PuntoAcopio;
import pe.upeu.ecorecicla.dominio.Ruta;
import pe.upeu.ecorecicla.dominio.RutaPunto;
import pe.upeu.ecorecicla.repositorio.PesajeRepository;
import pe.upeu.ecorecicla.repositorio.PuntoAcopioRepository;
import pe.upeu.ecorecicla.repositorio.RutaPuntoRepository;
import pe.upeu.ecorecicla.repositorio.RutaRepository;
import pe.upeu.ecorecicla.seguridad.Privilegio;
import pe.upeu.ecorecicla.seguridad.PrivilegioRepository;
import pe.upeu.ecorecicla.seguridad.Rol;
import pe.upeu.ecorecicla.seguridad.RolRepository;
import pe.upeu.ecorecicla.seguridad.Usuario;
import pe.upeu.ecorecicla.seguridad.UsuarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Carga al iniciar los privilegios, la matriz rol-privilegio, los usuarios de prueba y los datos del anexo.
 * La asignacion de privilegios a cada rol vive aqui y en la tabla rol_privilegio, no en los controladores:
 * agregar un rol nuevo (por ejemplo AUDITOR) solo exige una entrada en MATRIZ.
 */
@Component
public class SemillaDeDatos implements CommandLineRunner {

	static final Map<String, String> PRIVILEGIOS = new LinkedHashMap<>();
	static final Map<String, List<String>> MATRIZ = new LinkedHashMap<>();

	static {
		PRIVILEGIOS.put("PUNTO_LEER", "Consultar la ruta y los puntos de acopio");
		PRIVILEGIOS.put("PESAJE_LEER", "Consultar los pesajes de un punto");
		PRIVILEGIOS.put("PESAJE_CREAR", "Registrar un pesaje nuevo");
		PRIVILEGIOS.put("PESAJE_EDITAR", "Corregir un pesaje existente");
		PRIVILEGIOS.put("PESAJE_EDITAR_AJENOS", "Corregir pesajes de otros operarios y de dias anteriores");
		PRIVILEGIOS.put("PESAJE_ANULAR", "Anular un pesaje");
		PRIVILEGIOS.put("RUTA_CERRAR", "Cerrar la ruta del dia");
		PRIVILEGIOS.put("PUNTO_GESTIONAR", "Crear puntos de acopio y asignar rutas");

		MATRIZ.put("OPERARIO", List.of("PUNTO_LEER", "PESAJE_LEER", "PESAJE_CREAR", "PESAJE_EDITAR"));
		MATRIZ.put("SUPERVISOR", List.of("PUNTO_LEER", "PESAJE_LEER", "PESAJE_CREAR", "PESAJE_EDITAR",
				"PESAJE_EDITAR_AJENOS", "PESAJE_ANULAR", "RUTA_CERRAR"));
		MATRIZ.put("ADMINISTRADOR", List.of("PUNTO_LEER", "PESAJE_LEER", "PUNTO_GESTIONAR"));
	}

	private final PrivilegioRepository privilegios;
	private final RolRepository roles;
	private final UsuarioRepository usuarios;
	private final PuntoAcopioRepository puntos;
	private final RutaRepository rutas;
	private final RutaPuntoRepository rutaPuntos;
	private final PesajeRepository pesajes;
	private final PasswordEncoder encoder;

	public SemillaDeDatos(PrivilegioRepository privilegios, RolRepository roles, UsuarioRepository usuarios,
						  PuntoAcopioRepository puntos, RutaRepository rutas, RutaPuntoRepository rutaPuntos,
						  PesajeRepository pesajes, PasswordEncoder encoder) {
		this.privilegios = privilegios;
		this.roles = roles;
		this.usuarios = usuarios;
		this.puntos = puntos;
		this.rutas = rutas;
		this.rutaPuntos = rutaPuntos;
		this.pesajes = pesajes;
		this.encoder = encoder;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (usuarios.count() > 0) {
			return;
		}
		Map<String, Privilegio> catalogo = PRIVILEGIOS.entrySet().stream()
				.map(e -> privilegios.save(new Privilegio(e.getKey(), e.getValue())))
				.collect(Collectors.toMap(Privilegio::getCodigo, p -> p));
		Map<String, Rol> rolesPorNombre = new LinkedHashMap<>();
		MATRIZ.forEach((nombre, codigos) -> rolesPorNombre.put(nombre, roles.save(new Rol(nombre,
				codigos.stream().map(catalogo::get).collect(Collectors.toSet())))));

		String clave = encoder.encode("Eco2026*");
		Usuario operario1 = usuarios.save(new Usuario("operario1", clave, "Luis Quispe Mamani", rolesPorNombre.get("OPERARIO")));
		Usuario operario2 = usuarios.save(new Usuario("operario2", clave, "Rosa Huanca Condori", rolesPorNombre.get("OPERARIO")));
		Usuario supervisor = usuarios.save(new Usuario("supervisor1", clave, "Carmen Flores Rojas", rolesPorNombre.get("SUPERVISOR")));
		usuarios.save(new Usuario("admin", clave, "Administrador del Sistema", rolesPorNombre.get("ADMINISTRADOR")));

		List<PuntoAcopio> p = new ArrayList<>(puntos.saveAll(List.of(
				new PuntoAcopio("Textiles Andinos S.A.C.", "Av. Industrial 120", "Centro", "Carton"),
				new PuntoAcopio("Panaderia La Espiga", "Jr. Comercio 455", "Centro", "Carton"),
				new PuntoAcopio("Clinica San Gabriel", "Av. Salud 780", "Centro", "Papel"),
				new PuntoAcopio("Hotel Plaza Mayor", "Plaza de Armas 12", "Centro", "Vidrio"),
				new PuntoAcopio("Embotelladora Rio Claro", "Km 4 Carretera Norte", "Norte", "Plastico PET"),
				new PuntoAcopio("Supermercado El Ahorro", "Av. Los Pinos 301", "Norte", "Carton"),
				new PuntoAcopio("Imprenta Grafica Norte", "Jr. Los Olivos 88", "Norte", "Papel"),
				new PuntoAcopio("Restaurante Sabor Criollo", "Av. Brasil 950", "Norte", "Vidrio"),
				new PuntoAcopio("Metalurgica del Sur", "Parque Industrial Mz C", "Sur", "Metal"),
				new PuntoAcopio("Colegio Santa Rosa", "Av. Educacion 210", "Sur", "Papel"),
				new PuntoAcopio("Distribuidora Bebidas Sur", "Jr. Almacen 66", "Sur", "Plastico PET"),
				new PuntoAcopio("Ferreteria Central", "Av. Construccion 540", "Sur", "Metal"))));

		LocalDate hoy = LocalDate.now();
		LocalDate ayer = hoy.minusDays(1);
		Ruta rutaHoy = rutas.save(new Ruta(hoy, operario1, supervisor, false));
		Ruta rutaAyer = rutas.save(new Ruta(ayer, operario1, supervisor, true));
		for (int i = 0; i < 6; i++) {
			rutaPuntos.save(new RutaPunto(rutaHoy, p.get(i), i + 1));
		}
		for (int i = 0; i < 4; i++) {
			rutaPuntos.save(new RutaPunto(rutaAyer, p.get(6 + i), i + 1));
		}

		// Ruta de hoy (abierta): puntos 1 a 3 con pesajes, 4 a 6 sin pesar (RN-04)
		pesaje(rutaHoy, p.get(0), operario1, hoy.atTime(8, 15), "Plastico PET", 52.5, 4.5, null);
		Pesaje anulado = pesaje(rutaHoy, p.get(0), operario1, hoy.atTime(8, 20), "Carton", 31.0, 2.0, "Contenedor equivocado");
		anulado.anular("Error de digitacion en el peso");
		pesajes.save(anulado);
		pesaje(rutaHoy, p.get(1), operario1, hoy.atTime(8, 50), "Carton", 40.0, 3.0, null);
		pesaje(rutaHoy, p.get(1), operario2, hoy.atTime(9, 5), "Vidrio", 80.0, 10.0, "Registrado por otro operario");
		pesaje(rutaHoy, p.get(2), operario1, ayer.atTime(17, 30), "Papel", 120.0, 15.0, "Rezagado del turno anterior");
		pesaje(rutaHoy, p.get(2), operario1, hoy.atTime(9, 30), "Papel", 45.0, 5.0, null);

		// Ruta de ayer (cerrada): permite demostrar RN-01
		pesaje(rutaAyer, p.get(6), operario1, ayer.atTime(8, 0), "Papel", 60.0, 6.0, null);
		pesaje(rutaAyer, p.get(6), operario1, ayer.atTime(8, 10), "Carton", 35.0, 3.0, null);
		pesaje(rutaAyer, p.get(6), operario2, ayer.atTime(8, 25), "Papel", 22.0, 2.0, null);
		pesaje(rutaAyer, p.get(7), operario1, ayer.atTime(9, 0), "Vidrio", 90.0, 12.0, null);
		pesaje(rutaAyer, p.get(7), operario1, ayer.atTime(9, 20), "Vidrio", 70.0, 12.0, "Botellas verdes");
		pesaje(rutaAyer, p.get(8), operario1, ayer.atTime(10, 0), "Metal", 200.0, 25.0, null);
		pesaje(rutaAyer, p.get(8), operario1, ayer.atTime(10, 30), "Metal", 150.0, 20.0, null);
		pesaje(rutaAyer, p.get(9), operario1, ayer.atTime(11, 0), "Papel", 48.0, 4.0, null);
		pesaje(rutaAyer, p.get(9), operario2, ayer.atTime(11, 15), "Carton", 55.0, 5.0, null);
	}

	private Pesaje pesaje(Ruta ruta, PuntoAcopio punto, Usuario operario, LocalDateTime fechaHora, String material,
						  double bruto, double tara, String observacion) {
		double neto = Math.round((bruto - tara) * 100.0) / 100.0;
		double humedad = Math.round((5 + (Math.abs(material.hashCode()) % 140) / 10.0) * 10.0) / 10.0;
		return pesajes.save(new Pesaje(ruta, punto, operario, fechaHora, material, bruto, tara, neto, observacion, humedad));
	}
}
