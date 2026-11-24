package ies.puerto.peliculas.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ies.puerto.peliculas.controller.api.PeliculaEdicionRequest;
import ies.puerto.peliculas.controller.api.PeliculaRequest;
import ies.puerto.peliculas.controller.api.PeliculaResponse;
import ies.puerto.peliculas.controller.mapper.PeliculaApiMapper;
import ies.puerto.peliculas.model.Pelicula;
import ies.puerto.peliculas.service.PeliculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@RestController
@Profile("jdbc")
@RequestMapping("/api/v1/jdbc/peliculas")
@Tag(
        name = "Películas JDBC",
        description = "Catálogo de películas con SQLite y JDBC"
)
public class PeliculaJdbcController {

    private final PeliculaService service;
    private final PeliculaApiMapper apiMapper;

    public PeliculaJdbcController(
            PeliculaService service,
            PeliculaApiMapper apiMapper
    ) {
        this.service = service;
        this.apiMapper = apiMapper;
    }

    @Operation(
            summary = "Listar películas",
            description = "Devuelve todas las películas ordenadas por código."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de películas; puede estar vacía",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = PeliculaResponse.class
                                    )
                            )
                    )
            )
    })
    @GetMapping
    public List<PeliculaResponse> listar() {
        return apiMapper.aRespuestas(service.listar());
    }

    @Operation(
            summary = "Consultar una película",
            description = """
                    Busca una película por su código,
                    con formato PEL seguido de tres dígitos.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Película encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PeliculaResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El código no cumple el formato requerido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una película con ese código",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @GetMapping("/{codigo}")
    public ResponseEntity<?> obtener(
            @PathVariable
            @Pattern(regexp = "^PEL[0-9]{3}$")
            String codigo
    ) {
        Optional<Pelicula> resultado = service.obtener(codigo);

        if (resultado.isEmpty()) {
            return noEncontrada(codigo);
        }

        PeliculaResponse respuesta = apiMapper.aRespuesta(resultado.get());

        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Crear una película",
            description = """
                    Crea una película con código único.
                    El título es obligatorio y admite hasta 120 caracteres.
                    El año debe estar entre 1888 y 2100.
                    La duración debe estar entre 1 y 1000 minutos.
                    Devuelve la ruta de la película creada en la cabecera Location.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Película creada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PeliculaResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cuerpo ausente, JSON incorrecto o datos inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe una película con ese código",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody PeliculaRequest peticion
    ) {
        Optional<Pelicula> resultado = service.crear(
                apiMapper.aDatosCreacion(peticion)
        );

        if (resultado.isEmpty()) {
            ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    "Ya existe una película con el código " + peticion.codigo()
            );

            return ResponseEntity.status(HttpStatus.CONFLICT).body(problema);
        }

        PeliculaResponse respuesta = apiMapper.aRespuesta(resultado.get());

        URI ubicacion = URI.create(
                "/api/v1/jdbc/peliculas/" + respuesta.codigo()
        );

        return ResponseEntity.created(ubicacion).body(respuesta);
    }

    @Operation(
            summary = "Actualizar una película",
            description = """
                    Sustituye el título, año y duración de una película existente.
                    El código se recibe en la URL, no en el cuerpo.
                    Si la película no existe, no se crea una nueva.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Película actualizada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PeliculaResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Código, cuerpo o datos inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una película con ese código",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @PutMapping("/{codigo}")
    public ResponseEntity<?> actualizar(
            @PathVariable
            @Pattern(regexp = "^PEL[0-9]{3}$")
            String codigo,
            @Valid @RequestBody PeliculaEdicionRequest peticion
    ) {
        Optional<Pelicula> resultado = service.actualizar(
                codigo,
                apiMapper.aDatosEdicion(peticion)
        );

        if (resultado.isEmpty()) {
            return noEncontrada(codigo);
        }

        PeliculaResponse respuesta = apiMapper.aRespuesta(resultado.get());

        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Eliminar una película",
            description = "Elimina una película existente a partir de su código."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Película eliminada; respuesta sin cuerpo",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El código no cumple el formato requerido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una película con ese código",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminar(
            @PathVariable
            @Pattern(regexp = "^PEL[0-9]{3}$")
            String codigo
    ) {
        boolean eliminada = service.eliminar(codigo);

        if (!eliminada) {
            return noEncontrada(codigo);
        }

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ProblemDetail> noEncontrada(String codigo) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                "No existe una película con el código " + codigo
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problema);
    }
}