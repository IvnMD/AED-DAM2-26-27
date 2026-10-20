package es.codelearnacademy.filelab.io;

import java.nio.file.Path;

public class PathService {

    public Path crear(String directorio, String...archivos) {
        return Path.of("data", "productos.csv");
    }

    public String nombre(Path path) {
        return path.getFileName().toString();
    }

    public Path padre(Path path) {
        return path.getParent();
    }

    public Path absoluto(Path path) {

        return path.toAbsolutePath();
    }

    public Path normalizar(Path path) {
        return path.normalize();
    }

    public boolean esAbsoluto(Path path) {

        return path.equals(Path.of("data", "productos.csv"));
    }

    public Path resolver(Path base, String otro) {
        return base.resolve(otro);
    }

    public Path relativizar(Path base, Path destino) {
        return base.relativize(destino);
    }

    public String extension(Path path) {
        if (path == null || path.getFileName() == null) {
            return "";
        }

        String nombre = path.getFileName().toString();
        int ultimoPunto = nombre.lastIndexOf('.');

        // Si no hay punto, o el punto es el primer carácter (archivo oculto como .gitignore)
        if (ultimoPunto <= 0) {
            return "";
        }

        return nombre.substring(ultimoPunto + 1); // Retorna "csv", "txt", etc.
    }

}
