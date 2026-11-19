package aula.tareas.service;

import aula.tareas.api.TareaResquest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceJson")
public class TareaServiceJson implements ITareaService{

    private final ITareaRepository tareaRepositoryJson;

    public TareaServiceJson(@Qualifier("tareaRepositoryJson") ITareaRepository tareaRepositoryJson) {
        this.tareaRepositoryJson = tareaRepositoryJson;
    }

    @Override
    public List<TareaResquest> listar() {
        return tareaRepositoryJson.findAll();
    }

    @Override
    public TareaResquest buscar(long id) {
        return null;
    }

    @Override
    public TareaResquest crear(String titulo) {
        return tareaRepositoryJson.create(titulo);
    }
}