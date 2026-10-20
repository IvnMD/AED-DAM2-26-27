package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.nio.file.Path;
import java.util.List;

public class DataBridgeService {

    private final RepositoryFactory repositoryFactory;

    public DataBridgeService(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    public int convert(FileFormat origenFormato, Path origen,
                       FileFormat destinoFormato, Path destino) {
        // 1. Obtenemos el repositorio de origen usando el método 'create' de la factoría
        IProductoRepository repoOrigen = repositoryFactory.create(origenFormato, origen);
        List<Producto> productos = repoOrigen.findAll();

        // 2. Obtenemos el repositorio de destino
        IProductoRepository repoDestino = repositoryFactory.create(destinoFormato, destino);

        // 3. Volcamos los productos uno a uno usando la API estándar del repositorio
        int insertados = 0;
        for (Producto producto : productos) {
            if (repoDestino.create(producto)) {
                insertados++;
            }
        }
        return insertados;
    }

    public int exportJsonToCsv(Path sourcePath, Path targetPath) {
        return convert(FileFormat.JSON, sourcePath, FileFormat.CSV, targetPath);
    }
}
