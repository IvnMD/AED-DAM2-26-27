package es.ejemplo.gestion.config;

import es.ejemplo.gestion.exception.AplicationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre una conexión en la base SQLite indicada por el constructor. */
public class ConexionBD {
    private final String url;
    private final Path path;

    public ConexionBD(String url) {
        this.url = url;
        this.path = Path.of(url.replace("jdbc:sqlite:", ""));
    }

    public Connection obtenerConexion() {
        if (!Files.exists(path)) {
            throw new AplicationException("No existe la base de datos: " + path);
        }
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new AplicationException("Error al obtener conexion con el BD", e);
        }
    }
}