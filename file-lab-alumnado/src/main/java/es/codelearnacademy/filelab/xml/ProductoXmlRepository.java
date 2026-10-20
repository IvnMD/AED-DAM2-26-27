package es.codelearnacademy.filelab.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ProductoXmlRepository
        extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final Path path;
    private final XmlMapper mapper;

    public ProductoXmlRepository(Path path) {
        this(path, new XmlMapper());
    }

    public ProductoXmlRepository(Path path, XmlMapper mapper) {
        this.path = path;
        this.mapper = mapper;
    }

    @Override
    protected Long getId(Producto producto) {
        return producto.id();
    }

    @Override
    protected List<Producto> readAll() throws IOException {
        // Si el fichero no existe, retornamos una lista vacía para permitir la primera inserción
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        // Leemos todo el fichero XML de golpe y lo deserializamos en un Array de Producto
        Producto[] productosArray = mapper.readValue(path.toFile(), Producto[].class);

        // Devolvemos una lista mutable (ArrayList) para que se puedan añadir/eliminar elementos en memoria
        return new ArrayList<>(Arrays.asList(productosArray));
    }

    @Override
    protected void writeAll(List<Producto> productos) throws IOException {
        // Creamos los directorios padres si no existieran previamente
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        // Escribimos la lista completa en el fichero estructurada bajo una etiqueta raíz automática
        // Jackson envolverá la lista en un nodo raíz por defecto (ej. <ArrayList><item>...</item></ArrayList>)
        mapper.writeValue(path.toFile(), productos);
    }
}

