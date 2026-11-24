package ies.puerto.peliculas.repository.jdbc;

import org.springframework.stereotype.Component;

import ies.puerto.peliculas.model.Pelicula;

@Component
class PeliculaEntityMapper {

    PeliculaEntity aEntity(Pelicula pelicula) {
        return new PeliculaEntity(
                pelicula.getIdentificador(),
                pelicula.getNombre(),
                pelicula.getAnioLanzamiento(),
                pelicula.getMinutosProyeccion()
        );
    }

    Pelicula aPelicula(PeliculaEntity fila) {
        return new Pelicula(
                fila.getCodigoFila(),
                fila.getTituloFila(),
                fila.getAnioFila(),
                fila.getMinutosFila()
        );
    }
}