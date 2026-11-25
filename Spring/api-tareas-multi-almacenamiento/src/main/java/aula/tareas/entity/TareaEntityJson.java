package aula.tareas.entity;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Objects;

public class TareaEntityJson {
    @JsonAlias("id")
    private Long idTarea;
    @JsonAlias("titulo")
    private String nombreTarea;

    @Override //Equals creado por mi
    public boolean equals(Object o) {
        if (!(o instanceof TareaEntityJson that)) return false;
        return Objects.equals(idTarea, that.idTarea);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTarea);
    }
}
