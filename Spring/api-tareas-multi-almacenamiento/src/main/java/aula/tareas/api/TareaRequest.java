package aula.tareas.api;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.dialect.function.TrimFunction;

public record TareaRequest(Long id, @NotBlank String titulo) {



    public TareaRequest {
        if (titulo != null) {
            titulo = titulo.strip(); // Strip es la verison moderna de Trim()
        }
    }

}
