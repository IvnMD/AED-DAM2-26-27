package es.ejemplo.biblioteca.dao;
import java.util.*;
import es.ejemplo.biblioteca.modelo.*;

public final class LibroDao implements ILibroDao {
    private final String url;
 
public LibroDao(String url) { this.url=Objects.requireNonNull(url); }
 
 @Override 
 public List<Libro> listar() {
    throw new UnsupportedOperationException("Pendiente: listar");
 }
 
 @Override 
 public Optional<Libro> buscarPorId(Integer id) {
  throw new UnsupportedOperationException("Pendiente: buscarPorId");
 }
 
 @Override
 public int insertar(Libro entidad) {
  throw new UnsupportedOperationException("Pendiente: insertar");
 }
 @Override
 public int actualizar(Libro entidad) {
  throw new UnsupportedOperationException("Pendiente: actualizar");
 }
 
 @Override
 public int eliminar(Integer id) {
  throw new UnsupportedOperationException("Pendiente: eliminar");
 }
}
