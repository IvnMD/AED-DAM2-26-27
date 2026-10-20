package es.codelearnacademy.filelab.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class PropertiesConfig {

    private final Path path;

    public PropertiesConfig(Path path) {
        this.path = path;
    }

    /**
     * Carga el archivo de propiedades en memoria.
     * Si el archivo no existe, devuelve un objeto Properties vacío.
     */
    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                props.load(reader);
            }
        }
        return props;
    }

    /**
     * Guarda el objeto Properties en el archivo físico.
     */
    private void saveProperties(Properties props) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            props.store(writer, "Configuración actualizada automáticamente");
        }
    }

    public Optional<String> get(String key) {
        try {
            Properties props = loadProperties();
            return Optional.ofNullable(props.getProperty(key));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public String getOrDefault(String key, String defaultValue) {
        try {
            Properties props = loadProperties();
            return props.getProperty(key, defaultValue);
        } catch (IOException e) {
            return defaultValue;
        }
    }

    public Map<String, String> findAll() {
        Map<String, String> map = new HashMap<>();
        try {
            Properties props = loadProperties();
            for (String stringName : props.stringPropertyNames()) {
                map.put(stringName, props.getProperty(stringName));
            }
        } catch (IOException e) {
            // Devuelve un mapa vacío si ocurre un error de lectura
        }
        return map;
    }

    public boolean put(String key, String value) {
        try {
            Properties props = loadProperties();
            props.setProperty(key, value);
            saveProperties(props);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean remove(String key) {
        try {
            Properties props = loadProperties();
            // Comprobamos si la clave existía para retornar el booleano correcto
            if (props.containsKey(key)) {
                props.remove(key);
                saveProperties(props);
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
