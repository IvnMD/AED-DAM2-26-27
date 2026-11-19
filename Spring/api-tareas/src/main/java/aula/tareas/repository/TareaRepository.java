package aula.tareas.repository;

import aula.tareas.api.TareaResquest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository()
public class TareaRepository implements ITareaRepository{
    private final AtomicLong secuencia = new AtomicLong();
    private final ConcurrentSkipListMap<Long, TareaResquest> tareas = new ConcurrentSkipListMap<>();

    @Override
    public TareaResquest create(String titulo) {
        long id = secuencia.incrementAndGet();
        var tarea = new TareaResquest(id, titulo.strip());
        tareas.put(id, tarea);
        return tarea;
    }

    @Override
    public boolean delete(long identificador) {
        tareas.remove(identificador);
        return true;
    }

    @Override
    public List<TareaResquest> findAll() {
        return List.copyOf(tareas.values());
    }

    @Override
    public TareaResquest findById(long identificador) {
        return tareas.get(identificador);
    }

    @Override
    public TareaResquest update(TareaResquest tareaResquest) {
        // buscar la tareaResquest por el id
        // actualizar la tareaResquest
        return null;
    }

}
