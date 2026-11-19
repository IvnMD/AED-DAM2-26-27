package aula.tareas.repository;

import aula.tareas.api.TareaResquest;

import java.util.List;

public class TareaRepositoryXml implements ITareaRepository{
    /**
     * @param titulo
     * @return
     */
    @Override
    public TareaResquest create(String titulo) {
        return null;
    }

    /**
     * @param identificador
     * @return
     */
    @Override
    public boolean delete(long identificador) {
        return false;
    }

    /**
     * @return
     */
    @Override
    public List<TareaResquest> findAll() {
        return List.of();
    }

    /**
     * @param identificador
     * @return
     */
    @Override
    public TareaResquest findById(long identificador) {
        return null;
    }

    /**
     * @param tareaResquest
     * @return
     */
    @Override
    public TareaResquest update(TareaResquest tareaResquest) {
        return null;
    }
}
