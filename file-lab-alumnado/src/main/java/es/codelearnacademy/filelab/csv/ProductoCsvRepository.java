package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductoCsvRepository
        extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final Path path;

    public ProductoCsvRepository(Path path) {
        this.path = path;
    }

    @Override
    protected Long getId(Producto producto) {
        return producto.id();
    }

    @Override
    protected List<Producto> readAll() throws IOException {
        List<Producto> productos = new ArrayList<>();

        // Fichero inexistente = repositorio vacío (permite crear el primer producto)
        if (!Files.exists(path)) {
            return productos;
        }

        // Configuración para mapear usando nombres de columnas y saltar la cabecera
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("id", "nombre", "precio", "stock")
                .setSkipHeaderRecord(true)
                .build();

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {

            for (CSVRecord fila : parser) {
                productos.add(new Producto(
                        Long.parseLong(fila.get("id")),
                        fila.get("nombre"),
                        Double.parseDouble(fila.get("precio")),
                        Integer.parseInt(fila.get("stock"))));
            }
        }
        return productos;
    }

    @Override
    protected void writeAll(List<Producto> productos) throws IOException {
        // Configuración para añadir automáticamente la cabecera al escribir el archivo
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("id", "nombre", "precio", "stock")
                .build();


        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (Producto producto : productos) {
                printer.printRecord(
                        producto.id(),
                        producto.nombre(),
                        producto.precio(),
                        producto.stock()
                );
            }
        }
    }
}
