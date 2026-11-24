package ies.puerto.peliculas.service.modelo;

public record DatosCreacionPelicula(
        String referenciaSolicitada,
        String nombreSolicitado,
        int anioSolicitado,
        int minutosSolicitados
) {
}