package aula.tareas.repository;

import aula.tareas.api.TareaResquest;

import java.util.List;

public interface ITareaRepository {
    public TareaResquest create(String titulo);
    public boolean delete(long identificador);
    public List<TareaResquest> findAll();
    public TareaResquest findById(long identificador);
    public TareaResquest update(TareaResquest tareaResquest);
}
