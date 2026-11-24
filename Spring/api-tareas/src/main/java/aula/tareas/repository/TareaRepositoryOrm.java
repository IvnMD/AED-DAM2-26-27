package aula.tareas.repository;

import aula.tareas.api.TareaResquest;
import aula.tareas.entity.TareaEntity;
import aula.tareas.modelo.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositoryOrm")
public class TareaRepositoryOrm implements ITareaRepository {

    private final TareaRepositoryJpa tareaRepositoryJpa;

    public TareaRepositoryOrm(TareaRepositoryJpa tareaRepositoryJpa) {
        this.tareaRepositoryJpa = tareaRepositoryJpa;
    }

    /**
     * @param titulo
     * @return
     */
    @Override
    public TareaResquest create(String titulo) {
        TareaEntity tareaEntitySave = tareaRepositoryJpa.save(new TareaEntity(null, titulo));
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
        return List.of();
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
