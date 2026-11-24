package ies.puerto.peliculas.repository;

import java.util.List;
import java.util.Optional;

import ies.puerto.peliculas.model.Pelicula;

public interface PeliculaRepository {

    List<Pelicula> buscarTodas();

    Optional<Pelicula> buscarPorCodigo(String identificador);

    boolean insertarSiAusente(Pelicula pelicula);

    boolean actualizarSiExiste(Pelicula pelicula);

    boolean eliminar(String identificador);
}