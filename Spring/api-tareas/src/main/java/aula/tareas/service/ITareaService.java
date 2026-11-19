package aula.tareas.service;

import aula.tareas.api.TareaResquest;

import java.util.List;

public interface ITareaService {
    public List<TareaResquest> listar();

    public TareaResquest buscar(long id);

    public TareaResquest crear(String titulo);
}
