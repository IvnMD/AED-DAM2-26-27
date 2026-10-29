package es.ejemplo.clinica.infra;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private final String url;

    protected ConexionBD(String url) {
        this.url = url;
    }

    /**
     * Abre una conexión SQLite a partir de una URL.
     * Es el método que deben usar los DAO.
     */
    public static Connection abrir(String url) throws SQLException {
        return new ConexionBD(url).obtenerConexion();
    }

    /**
     * Abre una conexión usando la URL guardada en esta instancia.
     * Valida la URL, comprueba que el archivo SQLite exista y activa las claves foráneas.
     */
    public Connection obtenerConexion() throws SQLException {
        if (url == null || !url.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("URL SQLite no válida");
        }

        String ruta = url.substring("jdbc:sqlite:".length());

        if (!ruta.equals(":memory:") && !ruta.startsWith("file:") && !Files.isRegularFile(Path.of(ruta))) {
            throw new IllegalArgumentException("La base de datos no existe: " + ruta);
        }

        Connection cn = DriverManager.getConnection(url);

        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            cn.close();
            throw e;
        }

        return cn;
    }
}