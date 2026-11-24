package ies.puerto.peliculas.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ies.puerto.peliculas.model.Pelicula;
import ies.puerto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puerto.peliculas.service.modelo.DatosEdicionPelicula;

class PeliculaDominioMapperTest {

    private final PeliculaDominioMapper mapper = new PeliculaDominioMapper();

    @Test
    void aPeliculaDesdeCreacionConservaTodosLosDatos() {
        DatosCreacionPelicula datos = new DatosCreacionPelicula(
                "PEL003",
                "El castillo ambulante",
                2004,
                119
        );

        Pelicula pelicula = mapper.aPelicula(datos);

        assertAll(
                () -> assertEquals("PEL003", pelicula.getIdentificador()),
                () -> assertEquals("El castillo ambulante", pelicula.getNombre()),
                () -> assertEquals(2004, pelicula.getAnioLanzamiento()),
                () -> assertEquals(119, pelicula.getMinutosProyeccion())
        );
    }

    @Test
    void aPeliculaDesdeEdicionUsaElCodigoRecibidoYConservaLosDatos() {
        DatosEdicionPelicula datos = new DatosEdicionPelicula(
                "El castillo ambulante - actualizada",
                2004,
                120
        );

        Pelicula pelicula = mapper.aPelicula("PEL003", datos);

        assertAll(
                () -> assertEquals("PEL003", pelicula.getIdentificador()),
                () -> assertEquals(
                        "El castillo ambulante - actualizada",
                        pelicula.getNombre()
                ),
                () -> assertEquals(2004, pelicula.getAnioLanzamiento()),
                () -> assertEquals(120, pelicula.getMinutosProyeccion())
        );
    }
}