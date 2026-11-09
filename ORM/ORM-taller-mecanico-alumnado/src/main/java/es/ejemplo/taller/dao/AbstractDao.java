package es.ejemplo.taller.dao;

import jakarta.persistence.EntityManagerFactory;
import es.ejemplo.taller.orm.config.OrmFactory;
import java.util.List;
import java.util.function.Function;

// T = Clase de Dominio (ej. Vehiculo)
// E = Clase de Entidad (ej. VehiculoEntity)
// ID = Tipo de la clave primaria (ej. String o Integer)
public abstract class AbstractDao<T, E, ID> {

    protected final EntityManagerFactory managerFactory;
    private final Class<E> entityClass;
    private final Function<E, T> mapperToDomain;

    // Pasamos la clase de la entidad y la función de mapeo al constructor
    public AbstractDao(String url, Class<E> entityClass, Function<E, T> mapperToDomain) {
        this.managerFactory = OrmFactory.crear(url);
        this.entityClass = entityClass;
        this.mapperToDomain = mapperToDomain;
    }

    // Ahora el método es 100% genérico y dinámico
    protected List<T> findAll(String jpql) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(jpql, entityClass)
                    .getResultStream()
                    .map(mapperToDomain) // Usa el mapper correspondiente de forma dinámica
                    .toList();
        }
    }
}
