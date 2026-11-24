package aula.tareas.mapper;

import aula.tareas.api.TareaResquest;
import aula.tareas.entity.TareaEntityJdbc;
import aula.tareas.modelo.Tarea;

public class TareaMapperJdbc {

    static public TareaResquest toDomain(TareaEntityJdbc tareaEntityJdbc) {
        return null;
    }

     public static TareaEntityJdbc toJdbc(TareaResquest tareaResquest) {
         return null;
     }

     static public TareaEntityJdbc to(Tarea tarea) {
        if (tarea == null) {
            return null;
        }
        return new TareaEntityJdbc(tarea.getId(), tarea.getTitulo());
     }
}
