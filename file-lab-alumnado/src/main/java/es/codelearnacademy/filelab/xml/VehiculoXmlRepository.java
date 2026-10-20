package es.codelearnacademy.filelab.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.model.Vehiculo;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IVehiculoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VehiculoXmlRepository
        extends AbstractFileRepository<Vehiculo, String>
        implements IVehiculoRepository {

    private final Path path;
    private final XmlMapper mapper = new XmlMapper();

    public VehiculoXmlRepository(Path path) {
        this.path = path;
    }

    @Override
    protected String getId(Vehiculo vehiculo) {
        throw new UnsupportedOperationException("Función no implementada");
    }

    @Override
    protected List<Vehiculo> readAll() throws IOException {
        // Si el fichero no existe, retornamos una lista vacía para permitir la primera inserción
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        // Leemos todo el fichero XML de golpe y lo deserializamos en un Array de Producto
        Vehiculo[] vehiculosArray = mapper.readValue(path.toFile(), Vehiculo[].class);

        // Devolvemos una lista mutable (ArrayList) para que se puedan añadir/eliminar elementos en memoria
        return new ArrayList<>(Arrays.asList(vehiculosArray));
    }

    @Override
    protected void writeAll(List<Vehiculo> vehiculos) throws IOException {
        // Creamos los directorios padres si no existieran previamente
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        // Escribimos la lista completa en el fichero estructurada bajo una etiqueta raíz automática
        // Jackson envolverá la lista en un nodo raíz por defecto (ej. <ArrayList><item>...</item></ArrayList>)
        mapper.writeValue(path.toFile(), vehiculos);
    }
}
