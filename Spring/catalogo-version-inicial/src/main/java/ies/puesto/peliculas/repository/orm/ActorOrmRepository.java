package ies.puesto.peliculas.repository.orm;

import ies.puesto.peliculas.repository.IActorRepository;
import ies.puesto.peliculas.model.*;
import ies.puesto.peliculas.model.error.RecursoAusente;
import ies.puesto.peliculas.entity.ActorEntity;
import ies.puesto.peliculas.mapper.ActorMapper;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
class ActorOrmRepository implements IActorRepository {
    private final ActorJpaRepository jpa;

    ActorOrmRepository(ActorJpaRepository jpa) {
        this.jpa = jpa;
    }

    private ActorEntity entidad(String idActor) {
        return jpa.findById(idActor).orElseThrow(() -> new RecursoAusente("Actor", idActor));
    }

    @Override
    public List<Actor> buscarTodos() {
        return jpa.findAll(Sort.by("idActor")).stream().map(ActorMapper::toDomain).toList();
    }

    @Override
    public Actor obtener(String idActor) {
        return ActorMapper.toDomain(entidad(idActor));
    }


    @Override
    public void eliminar(String idActor) {
        jpa.deleteById(idActor); // o jpa.delete(entidad(idActor)) si queremos que lo haga con la entidad
    }


    @Override
    public Actor crear(String idActor, DatosActor datosActor) {
        if (jpa.existsById(idActor)) {
            throw new Conflicto("Ya existe el actor con el id " + idActor);
        }
        ActorEntity actorEntity = ActorMapper.toEntity(idActor, datosActor);
        jpa.saveAndFlush(actorEntity); // Save and flush para las transacciones que sean solo de escritura
        return ActorMapper.toDomain(actorEntity);

    }


    @Override
    public Actor editar(String idActor, DatosActor datosActor) {
        if (!jpa.existsById(idActor)){
            throw new RecursoAusente("Actor", idActor);
        }
        ActorEntity actorEntity = ActorMapper.toEntity(idActor, datosActor);
        return ActorMapper.toDomain(jpa.saveAndFlush(actorEntity));
    }
}
