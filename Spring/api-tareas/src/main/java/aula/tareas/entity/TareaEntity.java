package aula.tareas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name="tarea_orm")
public class TareaEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String titulo;


    public TareaEntity() {}


    public TareaEntity(long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public TareaEntity(long id) {
        this.id = id;
    }

    public TareaEntity(Long o, String titulo) {

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
