package es.codelearnacademy.filelab.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class TextFileService {

    public boolean escribir(Path path, String contenido) {
        if (path == null || contenido == null) {
            return false;
        }

        try {
            Files.writeString(path, contenido, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
            return false;
        }
    }

    public String leer(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("El path no puede ser null");
        }

        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return "";
        }
    }

    public boolean escribirLineas(Path path, List<String> lineas) {
        if (path == null || lineas == null || lineas.isEmpty()) {
            return false;
        }
        try {
            Files.write(path, lineas, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
            return false;
        }
    }

    public List<String> leerLineas(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("El path no puede ser null");
        }

        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return List.of();
        }
    }

    public boolean anexar(Path path, String contenido) {
        if (path == null || contenido == null){
            return false;
        }

        try {
            // StandardOpenOption.APPEND añade al final del archivo
            // StandardOpenOption.CREATE crea el archivo si no existe
            Files.writeString(
                    path,
                    contenido,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE
            );
            return true;
        } catch (IOException e) {
            System.err.println("Error al anexar al archivo: " + e.getMessage());
            return false;
        }

    }
}
