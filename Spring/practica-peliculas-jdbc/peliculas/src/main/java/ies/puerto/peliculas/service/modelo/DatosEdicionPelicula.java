package ies.puerto.peliculas.service.modelo;

public record DatosEdicionPelicula(
        String nombreSolicitado,
        int anioSolicitado,
        int minutosSolicitados
) {
}