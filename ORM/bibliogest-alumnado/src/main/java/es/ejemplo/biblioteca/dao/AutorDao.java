package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Autor;
import es.ejemplo.biblioteca.orm.entity.AutorEntity;
import es.ejemplo.biblioteca.orm.mapper.AutorMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AutorDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(AutorDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public AutorDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public AutorDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    public List<Autor> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from AutorEntity a order by a.idAutor",
                            AutorEntity.class)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (RuntimeException e) {
            registrar("listar", e);
            return List.of();
        }
    }

    public Optional<Autor> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(AutorEntity.class, id))
                    .map(AutorMapper::toDomain);
        } catch (RuntimeException e) {
            registrar("buscarPorId", e);
            return Optional.empty();
        }
    }

    public int insertar(Autor dato) {
        if (!validar(dato)) {
            return 0;
        }

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity entidad = AutorMapper.toEntity(dato);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdAutor();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                throw ex;
            }
        }
    }

    public int actualizar(Autor dato) {
        if (dato == null || dato.getIdAutor() == null) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity c = em.find(AutorEntity.class, dato.getIdAutor());
                if (c == null) {
                    tx.commit();
                    return 0;
                }
                AutorEntity autorEntityActualizado = AutorMapper.toEntity(dato);
                c.setNombre(autorEntityActualizado.getNombre());
                c.setNacionalidad(autorEntityActualizado.getNacionalidad());
                c.setAnioNacimiento(autorEntityActualizado.getAnioNacimiento());
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("actualizar", ex);
                throw ex;
            }
        }
    }

    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity a = em.find(AutorEntity.class, id);
                if (a == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(a);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("eliminar", ex);
                throw ex;
            }
        }
    }

    private boolean validar(Autor dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank();
    }

    public List<Autor> buscarPorNacionalidad(String nacionalidad) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from AutorEntity a where a.nacionalidad = :nacionalidad",
                            AutorEntity.class)
                    .setParameter("nacionalidad", nacionalidad)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (RuntimeException e) {
            registrar("buscarPorNacionalidad", e);
            return List.of();
        }
    }

    public long contarLibrosPorAutor(int idAutor) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select count(l) from LibroEntity l where l.autor.idAutor = :idAutor",
                            Long.class)
                    .setParameter("idAutor", idAutor)
                    .getSingleResult();
        } catch (RuntimeException e) {
            registrar("contarLibrosPorAutor", e);
            return 0L;
        }
    }

    public List<Object[]> autoresConRecuento() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a.nombre, count(a) from AutorEntity a group by a.nombre order by min(a.id_autor)",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException e) {
            registrar("autoresConRecuento", e);
            return List.of();
        }
    }

    public List<Autor> autoresLeidosPorTresSocios() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.libro.autor from PrestamoEntity p group by p.libro.autor having count(p) > 3",
                            AutorEntity.class)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (RuntimeException e) {
            registrar("autoresLeidosPorTresSocios", e);
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
