package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepositoryJpa extends JpaRepository<Tarea, Integer> {
}
