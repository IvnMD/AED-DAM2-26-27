package aula.tareas.service;

import aula.tareas.api.TareaResquest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TareaServiceXml implements ITareaService {
    @Override
    public List<TareaResquest> listar() {
        return List.of();
    }

    @Override
    public TareaResquest buscar(long id) {
        return null;
    }

    @Override
    public TareaResquest crear(String titulo) {
        return null;
    }
}
