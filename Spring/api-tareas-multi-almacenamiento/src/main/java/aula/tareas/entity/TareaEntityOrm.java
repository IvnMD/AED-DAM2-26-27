package aula.tareas.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
@Entity
public class TareaEntityOrm {
    @Id
    private Long idTarea;

    @Column(length = 100)
    private String nombreTarea;

    
}
