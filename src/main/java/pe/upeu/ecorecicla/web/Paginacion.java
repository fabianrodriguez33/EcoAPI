package pe.upeu.ecorecicla.web;

/** Normaliza los parametros de pagina y tamanio de todos los listados. */
final class Paginacion {

	static final int TAMANIO_MAXIMO = 200;

	private Paginacion() {
	}

	static int pagina(int pagina) {
		return Math.max(0, pagina);
	}

	static int tamanio(int tamanio) {
		return Math.min(Math.max(1, tamanio), TAMANIO_MAXIMO);
	}
}
