package com.codelearn.tareas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorTareasTest {
    @Test
    void anadeUnaTarea() {
        var gestor = new GestorTareas();
        gestor.anadir("Aprender Maven");
        assertEquals(1, gestor.listar().size());
        assertEquals("Aprender Maven", gestor.listar().get(0));
    }

    @Test
    void rechazaTituloVacio() {
        var gestor = new GestorTareas();
        assertThrows(IllegalArgumentException.class, () -> gestor.anadir(" "));
    }

    @Test
    void rechazaTituloNull() {
        var gestor = new GestorTareas();
        assertThrows(IllegalArgumentException.class, () -> gestor.anadir(null));
    }

    @Test
    void listarDevuelveCopiaInmutable() {
        var gestor = new GestorTareas();
        gestor.anadir("Tarea 1");

        var copia = gestor.listar();
        assertThrows(UnsupportedOperationException.class, () -> copia.add("Otra"));
    }
}
