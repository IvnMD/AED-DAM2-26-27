package aula.tareas.repository;

import aula.tareas.api.TareaResquest;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository ("tareaRepositoryJdbc")
public class TareaRepositoryJdbc implements ITareaRepository {

    //data, tareas.sqlite3
    //data/tareas.sqlite3
    //jdbc:sqlite:data/tareas.sqlite3
    private String jdbcUrl;

    public TareaRepositoryJdbc() {
        this("jdbc:sqlite:data/tareas.sqlite3");
    }

    public TareaRepositoryJdbc(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        if (!validarUrl(jdbcUrl)){
            throw new RuntimeException("No se encuentra base de datos");
        }
    }

    public boolean validarUrl(String jdbcUrl) {
        String url = jdbcUrl.replace("jdbc:sqlite:", "");
        Path path = Path.of(url);
        return Files.exists(path);
    }


    /**
     * @param titulo
     * @return
     */
    @Override
    public TareaResquest create(String titulo) {
        return null;
    }

    /**
     * @param identificador
     * @return
     */
    @Override
    public boolean delete(long identificador) {
        return false;
    }

    /**
     * @return
     */
    @Override
    public List<TareaResquest> findAll() {

        String sql = "SELECT id, titulo,FROM tareas_jdbc ORDER BY id";
        List<TareaResquest> tareas = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tareas.add(new TareaResquest(
                        rs.getLong("id"),
                        rs.getString("titulo")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tareas;
    }

    /**
     * @param identificador
     * @return
     */
    @Override
    public TareaResquest findById(long identificador) {
        return null;
    }

    /**
     * @param tareaResquest
     * @return
     */
    @Override
    public TareaResquest update(TareaResquest tareaResquest) {
        return null;
    }
}
