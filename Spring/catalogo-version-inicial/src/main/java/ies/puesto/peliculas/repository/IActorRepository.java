package ies.puesto.peliculas.repository;

import ies.puesto.peliculas.model.Actor;
import ies.puesto.peliculas.model.DatosActor;

import java.util.List;

public interface IActorRepository {
    /**
     *
     * @return
     */
    List<Actor> buscarTodos();

    /**
     *
     * @param idActor
     * @return
     */
    Actor obtener(String idActor);

    /**
     *
     * @param idActor
     */
    void eliminar(String idActor);

    /**
     *
     * @param idActor
     * @param datosActor
     * @return
     */
    Actor crear(String idActor, DatosActor datosActor);

    /**
     * Funcion que modifica un actor dentro del repositorio
     * @param actor
     * @return
     */
    Actor editar(String idActor, DatosActor datosActor);
}
