package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Libro;

import es.ejemplo.biblioteca.orm.entity.AutorEntity;
import es.ejemplo.biblioteca.orm.entity.LibroEntity;
import es.ejemplo.biblioteca.orm.mapper.AutorMapper;
import es.ejemplo.biblioteca.orm.mapper.LibroMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LibroDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(LibroDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;
    
    public LibroDao(String url) {managerFactory=OrmFactory.crear(url);ownFactory=true;}
    
    public LibroDao(EntityManagerFactory factory) {managerFactory=factory;ownFactory=false;}
    private void registrar(String metodo,Exception ex) {LOG.log(Level.WARNING,metodo+" no se pudo completar: "+ex.getMessage());}

    public List<Libro> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l order by l.idLibro",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }
    
    public Optional<Libro> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(LibroEntity.class, id))
                    .map(LibroMapper::toDomain);
        } catch (RuntimeException e) {
            registrar("buscarPorId", e);
            return Optional.empty();
        }
    }
    
    public int insertar(Libro dato) {
        if(dato==null){
            return 0;
        }

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                LibroEntity entidad = LibroMapper.toEntity(dato);
                em.persist(entidad);
                em.flush(); // Necesario aquí para leer inmediatamente el ID generado
                tx.commit();
                return entidad.getIdLibro();

            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        } catch (RuntimeException e) {
            registrar("buscarPorId", e);
            return 0;
        }
    }
    
    public int actualizar(Libro dato) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                LibroEntity l = em.find(LibroEntity.class, dato.getIdAutor());
                if (l == null) {
                    tx.commit();
                    return 0;
                }
                LibroEntity libroEntityActualizado = LibroMapper.toEntity(dato);
                l.setTitulo(libroEntityActualizado.getTitulo());
                l.setIsbn(libroEntityActualizado.getIsbn()); // dirty checking
                l.setGenero(libroEntityActualizado.getGenero());
                l.setAnioPublicacion(libroEntityActualizado.getAnioPublicacion());
                l.setEjemplares(libroEntityActualizado.getEjemplares());
                l.setIdAutor(libroEntityActualizado.getIdAutor());
                tx.commit();           // Hibernate detecta el cambio
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
    
    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                LibroEntity a = em.find(LibroEntity.class, id); // prueba a fabricar un eliminar por email
                if (a == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(a);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
    private boolean validar(Libro dato) {return dato != null && dato.getTitulo()!=null && !dato.getTitulo().isBlank() && dato.getIsbn()!=null && !dato.getIsbn().isBlank() && dato.getGenero()!=null && dato.getIdAutor()!=null && dato.getEjemplares()!=null && dato.getEjemplares()>=0;}

    public List<Libro> buscarPorGenero(String genero) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l.genero = :genero order by l.titulo",
                            LibroEntity.class)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException e) {
            registrar("buscarPorGenero", e);
            return List.of();
        }
    }


    public List<Libro> buscarPorAutor(int idAutor) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l.autor.id = :idAutor",
                            LibroEntity.class)
                    .setParameter("idAutor", idAutor)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException e) {
        registrar("buscarPorGenero", e);
        return List.of();
        }
    }

    
    public List<Libro> buscarPorTitulo(String texto) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select titulo from LibroEntity where titulo = :titulo",
                            LibroEntity.class)
                    .setParameter("titulo", texto)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("buscarPorTitulo", ex);
            return List.of();
        }
    }

    public List<Libro> listarDisponibles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l.ejemplares > (select count(p) from PrestamoEntity p "
                                    + "where p.idLibro = l.idLibro and p.fechaDevolucion is null) order by l.titulo",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarDisponibles", ex);
            return List.of();
        }
    }


    public List<Libro> librosDesdeAnio(int anio, String genero) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l.anio >= :anio and l.genero = :genero "
                                    + "and l.ejemplares > (select count(p) from PrestamoEntity p "
                                    + "where p.libro = l and p.fechaDevolucion is null) order by l.titulo",
                            LibroEntity.class)
                    .setParameter("anio", anio)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("librosDesdeAnio", ex);
            return List.of();
        }
    }


    public List<String> generos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select distinct l.genero from LibroEntity l where l.genero is not null order by l.genero",
                            String.class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("generos", ex);
            return List.of();
        }
    }

    public List<Libro> nuncaPrestados() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l not in (select p.libro from PrestamoEntity p)",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("nuncaPrestados", ex);
            return List.of();
        }
    }

    public List<Libro> masPrestadosQueMedia() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where (select count(p) from PrestamoEntity p where p.libro = l) > "
                                    + "(select count(p2) * 1.0 / count(distinct l2) from PrestamoEntity p2, LibroEntity l2)",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("masPrestadosQueMedia", ex);
            return List.of();
        }
    }

    public List<Libro> reservadosSinDisponibilidad() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l.ejemplares <= (select count(p) from PrestamoEntity p "
                                    + "where p.libro = l and p.fechaDevolucion is null)",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("reservadosSinDisponibilidad", ex);
            return List.of();
        }
    }

    public List<Object[]> librosConAutor() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l.titulo, l.autor.nombre from LibroEntity l order by l.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("librosConAutor", ex);
            return List.of();
        }
    }

    public List<Object[]> contarPorGenero() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l.genero, count(l) from LibroEntity l group by l.genero order by l.genero",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("contarPorGenero", ex);
            return List.of();
        }
    }

    public List<Object[]> prestamosPorGeneroMinimo(long minimo) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.libro.genero, count(p) from PrestamoEntity p group by p.libro.genero "
                                    + "having count(p) >= :minimo order by count(p) desc",
                            Object[].class)
                    .setParameter("minimo", minimo)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("prestamosPorGeneroMinimo", ex);
            return List.of();
        }
    }

    public List<Object[]> disponibilidadPorLibro() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l.titulo, (l.ejemplares - (select count(p) from PrestamoEntity p "
                                    + "where p.libro = l and p.fechaDevolucion is null)) from LibroEntity l order by l.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("disponibilidadPorLibro", ex);
            return List.of();
        }
    }

    public List<Object[]> estadisticasGenero() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l.genero, count(l), min(l.ejemplares), max(l.ejemplares) from LibroEntity l group by l.genero",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("estadisticasGenero", ex);
            return List.of();
        }
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) {
            managerFactory.close();
        }
    }
}