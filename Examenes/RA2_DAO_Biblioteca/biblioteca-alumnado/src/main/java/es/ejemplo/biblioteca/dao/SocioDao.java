package es.ejemplo.biblioteca.dao;
import java.util.*;
import es.ejemplo.biblioteca.modelo.*;
/** Implementación pendiente: el alumnado debe completar los métodos JDBC. */
public final class SocioDao implements ISocioDao {
 private final String url;
 public SocioDao(String url) { this.url=Objects.requireNonNull(url); }
 @Override
 public List<Socio> listar() {
  throw new UnsupportedOperationException("Pendiente: listar");
 }
 @Override
 public Optional<Socio> buscarPorId(Integer id) {
  throw new UnsupportedOperationException("Pendiente: buscarPorId");
 }
 @Override
 public int insertar(Socio entidad) {
  throw new UnsupportedOperationException("Pendiente: insertar");
 }
 @Override
 public int actualizar(Socio entidad) {
  throw new UnsupportedOperationException("Pendiente: actualizar");
 }
 @Override
 public int eliminar(Integer id) {
  throw new UnsupportedOperationException("Pendiente: eliminar");
 }
 @Override
 public Optional<Socio> buscarPorEmail(String email) {
  throw new UnsupportedOperationException("Pendiente: buscarPorEmail");
 }
 @Override
 public List<Socio> listarAltasDesde(String fecha) {
  throw new UnsupportedOperationException("Pendiente: listarAltasDesde");
 }
}
