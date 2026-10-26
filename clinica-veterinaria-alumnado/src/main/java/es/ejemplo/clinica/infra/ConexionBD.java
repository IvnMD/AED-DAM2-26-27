package es.ejemplo.clinica.infra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private final String url;

    protected ConexionBD(String url) {
        this.url = url;
    }

    /**
     * Crea el directorio de una ruta SQLite normal y devuelve su URL absoluta.
     * No modifica las URLs especiales (por ejemplo, jdbc:sqlite::memory: o file: URI).
     */
//    public static String normalizarUrl(String url) {
//        if (url == null || !url.startsWith("jdbc:sqlite:")) return url;
//        String archivo = url.substring("jdbc:sqlite:".length());
//        if (archivo.isBlank() || archivo.equals(":memory:") || archivo.startsWith("file:")) return url;
//        try {
//            Path ruta = Paths.get(archivo);
//            if (!ruta.isAbsolute()) {
//                // IntelliJ puede arrancar desde la carpeta padre del módulo Maven.
//                Path cwd = Paths.get("").toAbsolutePath().normalize();
//                Path proyecto = cwd;
//                if (!Files.exists(proyecto.resolve("pom.xml")) &&
//                        Files.exists(proyecto.resolve("taller-mecanico-alumnado/pom.xml"))) {
//                    proyecto = proyecto.resolve("taller-mecanico-alumnado");
//                }
//                ruta = proyecto.resolve(ruta);
//            }
//            ruta = ruta.toAbsolutePath().normalize();
//            Path parent = ruta.getParent();
//            if (parent != null) Files.createDirectories(parent);
//            return "jdbc:sqlite:" + ruta;
//        } catch (IOException | RuntimeException e) {
//            throw new RuntimeException(e);
////            return url;
//        }
//    }

    public static Connection obtenerConexion() throws SQLException {
        if (url == null || !url.startsWith("jdbc:sqlite:")) throw new IllegalArgumentException("URL SQLite no válida");
        String ruta = url.substring("jdbc:sqlite:".length());
        if (!ruta.equals(":memory:") && !ruta.startsWith("file:") && !Files.isRegularFile(Path.of(ruta)))
            throw new IllegalArgumentException("La base de datos no existe: " + ruta);
        Connection cn = DriverManager.getConnection(url);
        try (Statement st = cn.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
        catch (SQLException e) { cn.close(); throw e; }
        return cn;
    }
}