# Recorrido de los datos en la API de películas

## 1. Organización de la aplicación

La aplicación utiliza el paquete raíz `ies.puerto.peliculas` y separa sus
responsabilidades en cuatro capas:

- Controlador: recibe peticiones HTTP, valida la entrada y construye las
  respuestas HTTP.
- Servicio: coordina las operaciones y devuelve resultados mediante
  `Optional` o `boolean`.
- Dominio: representa las películas del catálogo con la clase `Pelicula`.
- Repositorio: ejecuta las consultas SQL y convierte entre filas y objetos
  de dominio.

El servicio depende de la interfaz `PeliculaRepository`, no directamente de
`PeliculaJdbcRepository`.

El controlador y el repositorio JDBC se activan con el perfil `jdbc`.

La ruta base de la API es:

```text
/api/v1/jdbc/peliculas
```

La base de datos se encuentra en:

```text
data/peliculas.sqlite3
```

La aplicación se ejecuta desde el directorio que contiene `pom.xml`, ya que
la conexión utiliza una ruta relativa.

## 2. Nombres de los datos en cada representación

| Dato | API | Entrada al servicio | Dominio | Fila JDBC | Columna SQLite |
|---|---|---|---|---|---|
| Identificador | `codigo` | `referenciaSolicitada` | `identificador` | `codigoFila` | `pelicula_codigo` |
| Título | `titulo` | `nombreSolicitado` | `nombre` | `tituloFila` | `titulo_texto` |
| Año | `anioEstreno` | `anioSolicitado` | `anioLanzamiento` | `anioFila` | `estreno_anio` |
| Duración | `duracionMinutos` | `minutosSolicitados` | `minutosProyeccion` | `minutosFila` | `duracion_minutos` |

Los nombres cambian entre representaciones. Los mappers trasladan
explícitamente los valores mediante constructores.

Los records de entrada a la API usan `Integer` para el año y la duración.
Esto permite detectar valores ausentes mediante `@NotNull`. Las respuestas,
los datos de entrada al servicio, el dominio y la entidad JDBC usan `int`.

## 3. Recorrido de un POST

### 3.1. Petición HTTP

```http
POST /api/v1/jdbc/peliculas
Content-Type: application/json
```

Ejemplo de cuerpo:

```json
{
  "codigo": "PEL003",
  "titulo": "El castillo ambulante",
  "anioEstreno": 2004,
  "duracionMinutos": 119
}
```

### 3.2. Recepción y validación

Spring convierte el JSON en un `PeliculaRequest`.

El método `crear()` de `PeliculaJdbcController` recibe ese record mediante
`@Valid @RequestBody`.

Antes de ejecutar el método, se comprueban estas reglas:

- `codigo`: obligatorio y con el patrón `^PEL[0-9]{3}$`.
- `titulo`: obligatorio, no compuesto solo por espacios y con un máximo de
  120 caracteres.
- `anioEstreno`: obligatorio y comprendido entre 1888 y 2100.
- `duracionMinutos`: obligatorio y comprendido entre 1 y 1000.

Si la entrada es inválida, Spring responde con `400 Bad Request` y no se
realiza la operación de creación.

### 3.3. Conversión de API a datos del servicio

El controlador llama a:

```java
apiMapper.aDatosCreacion(peticion)
```

`PeliculaApiMapper` construye un `DatosCreacionPelicula`:

```text
codigo          → referenciaSolicitada
titulo          → nombreSolicitado
anioEstreno     → anioSolicitado
duracionMinutos → minutosSolicitados
```

El controlador entrega ese record a `PeliculaService.crear()`.

### 3.4. Conversión de datos del servicio a dominio

El servicio utiliza:

```java
dominioMapper.aPelicula(datos)
```

`PeliculaDominioMapper` construye una `Pelicula`:

```text
referenciaSolicitada → identificador
nombreSolicitado     → nombre
anioSolicitado       → anioLanzamiento
minutosSolicitados   → minutosProyeccion
```

El servicio no vuelve a validar los formatos ni los rangos.

### 3.5. Escritura en SQLite

El servicio llama a:

```java
repository.insertarSiAusente(pelicula)
```

La implementación `PeliculaJdbcRepository` utiliza
`PeliculaEntityMapper.aEntity()` para convertir el dominio en una
`PeliculaEntity`:

```text
identificador     → codigoFila
nombre            → tituloFila
anioLanzamiento   → anioFila
minutosProyeccion → minutosFila
```

Después ejecuta mediante `JdbcTemplate.update()`:

```sql
INSERT INTO peliculas
    (pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos)
VALUES (?, ?, ?, ?)
ON CONFLICT(pelicula_codigo) DO NOTHING;
```

Los parámetros se asignan en este orden:

1. `codigoFila` → `pelicula_codigo`.
2. `tituloFila` → `titulo_texto`.
3. `anioFila` → `estreno_anio`.
4. `minutosFila` → `duracion_minutos`.

El repositorio devuelve `true` si se ha insertado exactamente una fila.
Si el código ya existe, no inserta ninguna fila y devuelve `false`.

En la creación no interviene `PeliculaRowMapper`, ya que no se lee un
`ResultSet`.

### 3.6. Respuesta y decisión del estado HTTP

Si el repositorio devuelve `true`:

1. El servicio devuelve `Optional.of(pelicula)` con la misma película
   construida antes de escribir, sin volver a consultarla.
2. El controlador utiliza `PeliculaApiMapper.aRespuesta()`.
3. El mapper construye un `PeliculaResponse`.
4. Spring convierte ese record en JSON.
5. El controlador responde con `201 Created` y la cabecera:

```http
Location: /api/v1/jdbc/peliculas/PEL003
```

La conversión de salida es:

```text
identificador     → codigo
nombre            → titulo
anioLanzamiento   → anioEstreno
minutosProyeccion → duracionMinutos
```

Si el repositorio devuelve `false`:

1. El servicio devuelve `Optional.empty()`.
2. El controlador interpreta el resultado como un código duplicado.
3. Responde con `409 Conflict` y un `ProblemDetail` cuyo `detail` menciona
   el código solicitado.

### 3.7. Resumen del POST

```text
JSON
  → PeliculaRequest
  → PeliculaJdbcController.crear()
  → PeliculaApiMapper.aDatosCreacion()
  → DatosCreacionPelicula
  → PeliculaService.crear()
  → PeliculaDominioMapper.aPelicula()
  → Pelicula
  → PeliculaJdbcRepository.insertarSiAusente()
  → PeliculaEntityMapper.aEntity()
  → PeliculaEntity
  → JdbcTemplate.update()
  → SQLite
```

Retorno cuando la creación tiene éxito:

```text
Filas afectadas: 1
  → true
  → Optional<Pelicula> con valor
  → PeliculaApiMapper.aRespuesta()
  → PeliculaResponse
  → JSON con estado 201
```

## 4. Recorrido de un GET por código

### 4.1. Petición HTTP

```http
GET /api/v1/jdbc/peliculas/PEL001
```

No lleva cuerpo JSON. El identificador llega en la URL.

### 4.2. Recepción y validación del código

El método `obtener()` de `PeliculaJdbcController` recibe `codigo` mediante
`@PathVariable`.

La anotación `@Pattern` comprueba que cumple:

```text
^PEL[0-9]{3}$
```

Si el código tiene un formato incorrecto, Spring responde con `400 Bad Request`.

### 4.3. Paso por el servicio

El controlador llama a:

```java
service.obtener(codigo)
```

El servicio delega en:

```java
repository.buscarPorCodigo(codigo)
```

En esta operación no se crean `DatosCreacionPelicula` ni
`DatosEdicionPelicula`, y no interviene `PeliculaDominioMapper`.

Solo viaja el identificador hasta el repositorio:

```text
codigo de la URL
  → parámetro codigo del servicio
  → parámetro identificador del repositorio
  → parámetro ? de la consulta SQL
```

### 4.4. Consulta en SQLite

`PeliculaJdbcRepository` ejecuta mediante `JdbcTemplate.query()`:

```sql
SELECT pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos
FROM peliculas
WHERE pelicula_codigo = ?;
```

El valor recibido se pasa como parámetro, sin concatenarlo al SQL.

### 4.5. Conversión de la fila SQL a dominio

Si existe una fila, `PeliculaRowMapper.mapRow()` lee las columnas por nombre
desde el `ResultSet` y construye una `PeliculaEntity`:

```text
pelicula_codigo  → codigoFila
titulo_texto     → tituloFila
estreno_anio     → anioFila
duracion_minutos → minutosFila
```

El repositorio utiliza `stream().findFirst()` para obtener la primera fila,
si existe. No utiliza `queryForObject()`.

Después, `PeliculaEntityMapper.aPelicula()` convierte la entidad en dominio:

```text
codigoFila  → identificador
tituloFila  → nombre
anioFila    → anioLanzamiento
minutosFila → minutosProyeccion
```

El repositorio devuelve un `Optional<Pelicula>`, que el servicio entrega
al controlador.

### 4.6. Respuesta y decisión del estado HTTP

Si el `Optional` contiene una película:

1. El controlador llama a `PeliculaApiMapper.aRespuesta()`.
2. El mapper construye un `PeliculaResponse`.
3. Spring lo convierte en JSON.
4. El controlador responde con `200 OK`.

La conversión final es:

```text
identificador     → codigo
nombre            → titulo
anioLanzamiento   → anioEstreno
minutosProyeccion → duracionMinutos
```

Ejemplo:

```json
{
  "codigo": "PEL001",
  "titulo": "El viaje de Chihiro",
  "anioEstreno": 2001,
  "duracionMinutos": 125
}
```

Si el `Optional` está vacío:

1. El controlador interpreta que la película no existe.
2. Construye un `ProblemDetail` cuyo `detail` menciona el código.
3. Responde con `404 Not Found`.

### 4.7. Resumen del GET por código

Petición:

```text
Código de la URL
  → PeliculaJdbcController.obtener()
  → PeliculaService.obtener()
  → PeliculaJdbcRepository.buscarPorCodigo()
  → JdbcTemplate.query()
  → SQLite
```

Retorno cuando existe la película:

```text
Fila SQL / ResultSet
  → PeliculaRowMapper.mapRow()
  → PeliculaEntity
  → PeliculaEntityMapper.aPelicula()
  → Optional<Pelicula> con valor
  → PeliculaService
  → PeliculaJdbcController
  → PeliculaApiMapper.aRespuesta()
  → PeliculaResponse
  → JSON con estado 200
```

## 5. Responsabilidad de los estados HTTP

| Estado | Quién lo decide | Motivo |
|---|---|---|
| `200` | Controlador | Consulta o actualización correcta |
| `201` | Controlador | Película creada |
| `204` | Controlador | Película eliminada; respuesta sin cuerpo |
| `400` | Spring | Código, cuerpo o formato de entrada inválido |
| `404` | Controlador | El servicio indica que la película no existe |
| `409` | Controlador | El servicio indica que el código ya existe |
| `500` | Tratamiento de errores de Spring | Error inesperado, por ejemplo un fallo de acceso a SQLite |

El servicio no devuelve estados HTTP ni utiliza `ResponseEntity`,
`HttpStatus` o tipos de `controller.api`.

No hay un manejador global de excepciones: el controlador traduce los
resultados del servicio a respuestas HTTP para los casos de negocio.

## 6. Comprobación de la aplicación

### Ejecutar las pruebas

Desde el directorio que contiene `pom.xml`:

```bash
mvn clean test
```

Las pruebas de los mappers verifican las conversiones de datos.
Las pruebas de MockMvc verifican el controlador con un servicio simulado;
no comprueban el repositorio contra una base SQLite real.

### Arrancar con el perfil JDBC

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=jdbc
```

### Consultar el catálogo

```bash
curl -i http://localhost:8080/api/v1/jdbc/peliculas
```

### Comprobar la persistencia manualmente

1. Crear una película de prueba sin modificar `PEL001` ni `PEL002`.
2. Detener la aplicación.
3. Volver a arrancarla con el mismo fichero SQLite.
4. Consultar la película creada y comprobar sus valores.
5. Guardar la salida o una captura como evidencia.
6. Eliminar la película de prueba al terminar.

Esta comprobación requiere una evidencia propia; las pruebas con el servicio
simulado no demuestran persistencia tras reiniciar.