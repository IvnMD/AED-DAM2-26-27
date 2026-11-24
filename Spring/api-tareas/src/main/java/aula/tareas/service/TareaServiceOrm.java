package aula.tareas.service;

import aula.tareas.api.TareaResquest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceOrm")
public class TareaServiceOrm implements ITareaService{

    private final ITareaRepository tareaRepositoryOrm;

    public TareaServiceOrm(@Qualifier("tareaRepositoryOrm") ITareaRepository tareaRepositoryOrm) {
        this.tareaRepositoryOrm = tareaRepositoryOrm;
    }

    @Override
    public List<TareaResquest> listar() {
        return tareaRepositoryOrm.findAll();
    }

    @Override
    public TareaResquest buscar(long id) {
        return null;
    }

    @Override
    public TareaResquest crear(String titulo) {
        return tareaRepositoryOrm.create(titulo);
    }
}