package aula.tareas.service;

import aula.tareas.api.TareaResquest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceJdbc")
public class TareaServiceJdbc implements ITareaService{

    private final ITareaRepository tareaRepositoryJdbc;

    public TareaServiceJdbc(@Qualifier("tareaRepositoryJdbc") ITareaRepository tareaRepositoryJdbc) {
        this.tareaRepositoryJdbc = tareaRepositoryJdbc;
    }

    @Override
    public List<TareaResquest> listar() {
        return tareaRepositoryJdbc.findAll();
    }

    @Override
    public TareaResquest buscar(long id) {
        return null;
    }

    @Override
    public TareaResquest crear(String titulo) {
        return tareaRepositoryJdbc.create(titulo);
    }
}