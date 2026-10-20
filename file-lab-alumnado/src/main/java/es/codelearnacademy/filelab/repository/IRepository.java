package es.codelearnacademy.filelab.repository;

import es.codelearnacademy.filelab.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IRepository<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean create(T entity);
    boolean update(T entity);
    boolean delete(ID id);

}

