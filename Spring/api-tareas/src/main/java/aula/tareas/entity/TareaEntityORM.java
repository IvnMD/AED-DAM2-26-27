package aula.tareas.entity;

@Entity("tarea_orm")
public class TareaEntityORM {

    private long id;
    private String titulo;

    public TareaEntityORM(long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public TareaEntityORM(long id) {
        this.id = id;
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
