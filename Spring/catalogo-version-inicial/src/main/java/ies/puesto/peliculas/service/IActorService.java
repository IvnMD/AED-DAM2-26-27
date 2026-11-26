package ies.puesto.peliculas.service;

import ies.puesto.peliculas.model.Actor;
import ies.puesto.peliculas.model.DatosActor;

import java.util.List;

public interface IActorService {

    public List<Actor> listar();
    public Actor obtener(String id);
    public Actor crear(String idActor, DatosActor datosActor);
    public Actor editar(String idActor, DatosActor datosActor);
    public void eliminar(String name);
}
