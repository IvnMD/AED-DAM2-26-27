package ies.puerto.peliculas.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ies.puerto.peliculas.controller.api.PeliculaEdicionRequest;
import ies.puerto.peliculas.controller.api.PeliculaRequest;
import ies.puerto.peliculas.controller.api.PeliculaResponse;
import ies.puerto.peliculas.model.Pelicula;
import ies.puerto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puerto.peliculas.service.modelo.DatosEdicionPelicula;

class PeliculaApiMapperTest {

    private final PeliculaApiMapper mapper = new PeliculaApiMapper();

    @Test
    void aDatosCreacionConservaTodosLosDatos() {
        PeliculaRequest peticion = new PeliculaRequest(
                "PEL003",
                "El castillo ambulante",
                2004,
                119
        );

        DatosCreacionPelicula datos = mapper.aDatosCreacion(peticion);

        assertAll(
                () -> assertEquals("PEL003", datos.referenciaSolicitada()),
                () -> assertEquals(
                        "El castillo ambulante",
                        datos.nombreSolicitado()
                ),
                () -> assertEquals(2004, datos.anioSolicitado()),
                () -> assertEquals(119, datos.minutosSolicitados())
        );
    }

    @Test
    void aDatosEdicionConservaTodosLosDatos() {
        PeliculaEdicionRequest peticion = new PeliculaEdicionRequest(
                "El castillo ambulante - actualizada",
                2004,
                120
        );

        DatosEdicionPelicula datos = mapper.aDatosEdicion(peticion);

        assertAll(
                () -> assertEquals(
                        "El castillo ambulante - actualizada",
                        datos.nombreSolicitado()
                ),
                () -> assertEquals(2004, datos.anioSolicitado()),
                () -> assertEquals(120, datos.minutosSolicitados())
        );
    }

    @Test
    void aRespuestaConservaTodosLosDatos() {
        Pelicula pelicula = new Pelicula(
                "PEL001",
                "El viaje de Chihiro",
                2001,
                125
        );

        PeliculaResponse respuesta = mapper.aRespuesta(pelicula);

        assertAll(
                () -> assertEquals("PEL001", respuesta.codigo()),
                () -> assertEquals("El viaje de Chihiro", respuesta.titulo()),
                () -> assertEquals(2001, respuesta.anioEstreno()),
                () -> assertEquals(125, respuesta.duracionMinutos())
        );
    }

    @Test
    void aRespuestasConvierteTodasLasPeliculasYConservaElOrden() {
        List<Pelicula> peliculas = List.of(
                new Pelicula(
                        "PEL001",
                        "El viaje de Chihiro",
                        2001,
                        125
                ),
                new Pelicula(
                        "PEL002",
                        "Mi vecino Totoro",
                        1988,
                        86
                )
        );

        List<PeliculaResponse> respuestas = mapper.aRespuestas(peliculas);

        List<PeliculaResponse> esperadas = List.of(
                new PeliculaResponse(
                        "PEL001",
                        "El viaje de Chihiro",
                        2001,
                        125
                ),
                new PeliculaResponse(
                        "PEL002",
                        "Mi vecino Totoro",
                        1988,
                        86
                )
        );

        assertEquals(esperadas, respuestas);
    }

    @Test
    void aRespuestasDevuelveListaVaciaCuandoNoHayPeliculas() {
        List<PeliculaResponse> respuestas = mapper.aRespuestas(List.of());

        assertTrue(respuestas.isEmpty());
    }
}