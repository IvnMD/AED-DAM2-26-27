package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.model.Vehiculo;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IVehiculoRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VehiculoCsvRepository
        extends AbstractFileRepository<Vehiculo, String>
        implements IVehiculoRepository {

    private final Path path;

    public VehiculoCsvRepository(Path path) {
        this.path = path;
    }

    @Override
    protected String getId(Vehiculo vehiculo) {
        return vehiculo.matricula();
    }

    @Override
    protected List<Vehiculo> readAll() throws IOException {
        List<Vehiculo> vehiculos = new ArrayList<>();
        if (!Files.exists(path)) {
            return vehiculos;
        }

        List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
        // Saltamos la cabecera si existe
        boolean primera = true;
        for (String linea : lineas) {
            if (linea.isBlank()) continue;
            if (primera && linea.contains("matricula")) {
                primera = false;
                continue;
            }
            String[] partes = linea.split(",");
            if (partes.length >= 4) {
                vehiculos.add(new Vehiculo(
                        partes[0].trim(),
                        partes[1].trim(),
                        partes[2].trim(),
                        Integer.parseInt(partes[3].trim())
                ));
            }
        }
        return vehiculos;
    }

    @Override
    protected void writeAll(List<Vehiculo> vehiculos) throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add("matricula,marca,modelo,anio");
        for (Vehiculo v : vehiculos) {
            lineas.add(String.format("%s,%s,%s,%d", v.matricula(), v.marca(), v.modelo(), v.anio()));
        }
        Files.write(path, lineas, StandardCharsets.UTF_8);
    }

}
