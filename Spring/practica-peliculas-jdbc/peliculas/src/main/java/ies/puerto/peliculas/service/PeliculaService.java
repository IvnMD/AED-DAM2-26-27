package ies.puerto.peliculas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ies.puerto.peliculas.model.Pelicula;
import ies.puerto.peliculas.repository.PeliculaRepository;
import ies.puerto.peliculas.service.mapper.PeliculaDominioMapper;
import ies.puerto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puerto.peliculas.service.modelo.DatosEdicionPelicula;

@Service
public class PeliculaService {

    private final PeliculaRepository repository;
    private final PeliculaDominioMapper dominioMapper;

    public PeliculaService(
            PeliculaRepository repository,
            PeliculaDominioMapper dominioMapper
    ) {
        this.repository = repository;
        this.dominioMapper = dominioMapper;
    }

    public List<Pelicula> listar() {
        return repository.buscarTodas();
    }

    public Optional<Pelicula> obtener(String codigo) {
        return repository.buscarPorCodigo(codigo);
    }

    public Optional<Pelicula> crear(DatosCreacionPelicula datos) {
        Pelicula pelicula = dominioMapper.aPelicula(datos);

        boolean creada = repository.insertarSiAusente(pelicula);

        if (creada) {
            return Optional.of(pelicula);
        }

        return Optional.empty();
    }

    public Optional<Pelicula> actualizar(
            String codigo,
            DatosEdicionPelicula datos
    ) {
        Pelicula pelicula = dominioMapper.aPelicula(codigo, datos);

        boolean actualizada = repository.actualizarSiExiste(pelicula);

        if (actualizada) {
            return Optional.of(pelicula);
        }

        return Optional.empty();
    }

    public boolean eliminar(String codigo) {
        return repository.eliminar(codigo);
    }
}