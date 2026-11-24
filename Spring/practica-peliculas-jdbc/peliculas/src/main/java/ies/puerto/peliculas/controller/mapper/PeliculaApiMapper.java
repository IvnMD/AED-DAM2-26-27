package ies.puerto.peliculas.controller.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import ies.puerto.peliculas.controller.api.PeliculaEdicionRequest;
import ies.puerto.peliculas.controller.api.PeliculaRequest;
import ies.puerto.peliculas.controller.api.PeliculaResponse;
import ies.puerto.peliculas.model.Pelicula;
import ies.puerto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puerto.peliculas.service.modelo.DatosEdicionPelicula;

@Component
public class PeliculaApiMapper {

    public DatosCreacionPelicula aDatosCreacion(PeliculaRequest peticion) {
        return new DatosCreacionPelicula(
                peticion.codigo(),
                peticion.titulo(),
                peticion.anioEstreno(),
                peticion.duracionMinutos()
        );
    }

    public DatosEdicionPelicula aDatosEdicion(PeliculaEdicionRequest peticion) {
        return new DatosEdicionPelicula(
                peticion.titulo(),
                peticion.anioEstreno(),
                peticion.duracionMinutos()
        );
    }

    public PeliculaResponse aRespuesta(Pelicula pelicula) {
        return new PeliculaResponse(
                pelicula.getIdentificador(),
                pelicula.getNombre(),
                pelicula.getAnioLanzamiento(),
                pelicula.getMinutosProyeccion()
        );
    }

    public List<PeliculaResponse> aRespuestas(List<Pelicula> peliculas) {
        return peliculas.stream()
                .map(this::aRespuesta)
                .toList();
    }
}