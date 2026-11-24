package aula.tareas.modelo;

import java.util.ArrayList;

public class Tarea {

    private long id;
    private String titulo;

    public Tarea(String titulo, long id) {
        this.titulo = titulo;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


}
