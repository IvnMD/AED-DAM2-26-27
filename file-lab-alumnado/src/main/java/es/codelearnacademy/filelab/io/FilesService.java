package es.codelearnacademy.filelab.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.OptionalLong;

public class FilesService {

    public boolean existe(Path path) {
        return path.toFile().exists();
    }

    public Optional<Path> crearDirectorio(Path path) {
        if (path == null ) {
            return Optional.empty();
        }
        try {
            Path nuevoDirectorio =  Files.createDirectory(path);
            return Optional.of(nuevoDirectorio);
        } catch (IOException e) {
            System.err.println("Error al crear Directorio" + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Path> crearDirectorios(Path path) {
        if (path == null ) {
            return Optional.empty();
        }
        try {
            Path nuevosDirectorios =  Files.createDirectories(path);
            return Optional.of(nuevosDirectorios);
        } catch (IOException e) {
            System.err.println("Error al crear los directorios" + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Path> crearArchivo(Path path) {
        if (path == null ) {
            return Optional.empty();
        }
        try {
            Path nuevoArchivo = Files.createFile(path);
            return Optional.of(nuevoArchivo);
        } catch (IOException e) {
            System.err.println("Error al crear Archivo" + e.getMessage());
            return Optional.empty();
        }

    }

    public Optional<Path> copiar(Path origen, Path destino) {
        if (origen == null || destino == null) {
            return Optional.empty();
        }
        try {
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            return Optional.of(destino);
        } catch (IOException e) {
            System.err.println("Error al copiar el archivo" + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Path> mover(Path origen, Path destino) {
        if (origen == null || destino == null) {
            return Optional.empty();
        }
        try {
            Files.createDirectory(destino);
            Path nuevoPath = destino.resolve(origen.getFileName());
            Files.move(origen, nuevoPath, StandardCopyOption.ATOMIC_MOVE);
            return Optional.of(nuevoPath);
        } catch (IOException e) {
            System.err.println("Error al mover el archivo" + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean eliminar(Path path) {
        if (path == null) {
            return false;
        }
        return path.toFile().delete();
    }

    public OptionalLong tamanio(Path path) {
        if (path == null) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Files.size(path));
        } catch (IOException e) {
            System.err.println("Error al obtener el tamanyo del archivo" + e.getMessage());
            return OptionalLong.empty();
        }
    }
}
