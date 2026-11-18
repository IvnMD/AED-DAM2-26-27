package aula.tareas.service;

import aula.tareas.modelo.Tarea;

import java.util.List;

public interface ITareaService {
    public List<Tarea> listar();

    public Tarea buscar(long id);

    public Tarea crear(String titulo);
}
