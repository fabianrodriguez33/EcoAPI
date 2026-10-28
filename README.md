# EcoAPI

Servicio REST de EcoRecicla (Spring Boot 4, Java 21, Maven). Custodia rutas, puntos de acopio y pesajes.

## Ejecución

Un solo comando, sin instalar base de datos (H2 en memoria con datos semilla):

```bash
./mvnw spring-boot:run
```

Con Docker:

```bash
docker compose up -d --build
```

Queda en `http://localhost:8080`. Desde el emulador Android: `http://10.0.2.2:8080`.

Usuarios de prueba (contraseña `Eco2026*`): `operario1`, `operario2` (OPERARIO), `supervisor1` (SUPERVISOR), `admin` (ADMINISTRADOR), `auditor1` (AUDITOR).

## Estructura de paquetes (`pe.upeu.ecorecicla`)

- `config/`: `SecurityConfig`, `JwtAuthenticationFilter`, `JwtService`, `ManejadorDeErrores`, `SemillaDeDatos`.
- `seguridad/`: `Usuario`, `Rol`, `Privilegio`, `UsuarioRepository`, `AuthController`.
- `dominio/`: entidades `PuntoAcopio`, `Ruta`, `RutaPunto`, `Pesaje`.
- `repositorio/`: acceso a datos.
- `servicio/`: reglas RN-01 a RN-04 y transacciones.
- `web/`: controladores bajo `/api/v1`; `web/dto/` con peticiones, respuestas, paginación y formato de error.

## Contrato del API (`/api/v1`)

| Operación | Ruta | Privilegio | Éxito |
|---|---|---|---|
| Inicio de sesión | `POST /auth/login` | público | 200 |
| Usuario actual | `GET /auth/me` | autenticado | 200 |
| Ruta del día | `GET /rutas/dia?fecha=YYYY-MM-DD` | PUNTO_LEER | 200 |
| Cerrar ruta (RN-04) | `POST /rutas/{id}/cerrar` | RUTA_CERRAR | 200 |
| Omitir punto | `POST /rutas/{id}/puntos/{puntoId}/omision` | PESAJE_CREAR | 200 |
| Listar puntos | `GET /puntos?zona=&pagina=&tamanio=` | PUNTO_LEER | 200 |
| Crear punto | `POST /puntos` | PUNTO_GESTIONAR | 201 |
| Pesajes de un punto | `GET /puntos/{id}/pesajes?pagina=&tamanio=` | PESAJE_LEER | 200 |
| Registrar pesaje (RN-01, RN-03) | `POST /pesajes` | PESAJE_CREAR | 201 |
| Corregir pesaje (RN-01, RN-02, RN-03) | `PUT /pesajes/{id}` | PESAJE_EDITAR | 200 |
| Anular pesaje (marca, no borra) | `DELETE /pesajes/{id}?motivo=` | PESAJE_ANULAR | 204 |

Errores: 400 (con `validationErrors`), 401 (token ausente, alterado o vencido), 403 (sin privilegio o RN-02), 404, 409 (RN-01, RN-04), 500. Todos usan el formato `timestamp/status/error/message/path/validationErrors`. Los listados usan el envoltorio `contenido/pagina/tamanio/totalElementos/totalPaginas/ultima`.

## Roles y privilegios

La matriz vive en la base (`rol_privilegio`), cargada por `SemillaDeDatos`. Los controladores solo usan `hasAuthority('PRIVILEGIO')`; nunca el nombre del rol.

| Privilegio | OPERARIO | SUPERVISOR | ADMIN |
|---|---|---|---|
| PUNTO_LEER, PESAJE_LEER | sí | sí | sí |
| PESAJE_CREAR | sí | sí | no |
| PESAJE_EDITAR | propios, mismo día | sí | no |
| PESAJE_EDITAR_AJENOS | no | sí | no |
| PESAJE_ANULAR, RUTA_CERRAR | no | sí | no |
| PUNTO_GESTIONAR | no | no | sí |

El rol AUDITOR solo tiene `PUNTO_LEER` y `PESAJE_LEER`: se agregó únicamente en la matriz de `SemillaDeDatos`, sin tocar el filtro de seguridad ni los controladores.

`PESAJE_EDITAR_AJENOS` es un privilegio adicional: permite a RN-02 distinguir quién corrige pesajes ajenos sin comparar nombres de rol.

## Decisiones

- JWT HS256 firmado con `eco.jwt.secreto` (variable `ECO_JWT_SECRETO`), vigencia de una hora, con id, usuario, rol y privilegios. No hay renovación.
- Filtro JWT sin sesión: token inválido o vencido responde 401 directamente.
- Contraseñas con BCrypt.
