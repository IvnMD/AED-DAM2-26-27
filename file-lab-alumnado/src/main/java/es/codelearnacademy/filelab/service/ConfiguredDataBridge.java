package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.config.PropertiesConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class ConfiguredDataBridge {

    private final PropertiesConfig config;
    private final DataBridgeService bridge;

    public ConfiguredDataBridge(PropertiesConfig config, DataBridgeService bridge) {
        this.config = config;
        this.bridge = bridge;
    }
    /**
     * Lee las rutas de los archivos de origen y destino desde la configuración
     * y ejecuta la transferencia de datos.
     *
     * @return El número de registros / elementos transferidos con éxito.
     */
    public int execute() {
        // 1. Extraemos las propiedades de configuración con las claves del sistema
        Optional<String> originProp = config.get("bridge.source.path");
        Optional<String> targetProp = config.get("bridge.target.path");

        // 2. Validamos que ambas rutas existan en el archivo de propiedades
        if (originProp.isEmpty() || targetProp.isEmpty()) {
            // Si falta configuración, no se pueden transferir datos (0 registros procesados)
            return 0;
        }

        // 3. Convertimos los valores de texto a objetos Path válidos
        Path sourcePath = Paths.get(originProp.get());
        Path targetPath = Paths.get(targetProp.get());

        try {
            // 4. Delegamos la transferencia al servicio de pasarela y devolvemos el resultado
            return bridge.exportJsonToCsv(sourcePath, targetPath);
        } catch (Exception e) {
            // En caso de error de E/S o formato durante el puenteo de datos
            return 0;
        }
    }
}
