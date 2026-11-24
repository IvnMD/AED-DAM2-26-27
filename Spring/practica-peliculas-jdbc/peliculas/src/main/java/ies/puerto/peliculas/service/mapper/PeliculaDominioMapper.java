package ies.puerto.peliculas.service.mapper;

import org.springframework.stereotype.Component;

import ies.puerto.peliculas.model.Pelicula;
import ies.puerto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puerto.peliculas.service.modelo.DatosEdicionPelicula;

@Component
public class PeliculaDominioMapper {

    public Pelicula aPelicula(DatosCreacionPelicula datos) {
        return new Pelicula(
                datos.referenciaSolicitada(),
                datos.nombreSolicitado(),
                datos.anioSolicitado(),
                datos.minutosSolicitados()
        );
    }

    public Pelicula aPelicula(String identificador, DatosEdicionPelicula datos) {
        return new Pelicula(
                identificador,
                datos.nombreSolicitado(),
                datos.anioSolicitado(),
                datos.minutosSolicitados()
        );
    }
}