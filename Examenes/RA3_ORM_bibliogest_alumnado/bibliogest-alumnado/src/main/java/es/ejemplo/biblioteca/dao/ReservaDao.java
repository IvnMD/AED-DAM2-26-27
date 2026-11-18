package es.ejemplo.biblioteca.dao;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ejemplo.biblioteca.modelo.Reserva;
import es.ejemplo.biblioteca.orm.entity.ReservaEntity;
import es.ejemplo.biblioteca.orm.mapper.ReservaMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

/**
 * DAO responsable de las operaciones de persistencia y consultas sobre reservas.
 */
public class ReservaDao implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(ReservaDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea un DAO que gestiona su propio EntityManagerFactory.
     *
     * @param url URL JDBC de la base de datos SQLite
     */
    public ReservaDao(String url) {
        this.managerFactory = es.ejemplo.biblioteca.orm.config.OrmFactory.crear(url);
        this.ownFactory = true;
    }

    /**
     * Crea un DAO que reutiliza un EntityManagerFactory existente.
     *
     * @param managerFactory fábrica compartida de gestores de entidades
     */
    public ReservaDao(EntityManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
        this.ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Obtiene todas las reservas ordenadas por identificador.
     *
     * @return lista de reservas; vacía si no hay datos o se produce un error
     */
    public List<Reserva> listar() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT r FROM ReservaEntity r ORDER BY r.idReserva",
                            ReservaEntity.class)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    /**
     * Busca una reserva por su identificador.
     *
     * @param id identificador de la reserva
     * @return reserva encontrada u Optional vacío
     */
    public Optional<Reserva> buscarPorId(Integer id) {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(ReservaEntity.class, id))
                    .map(ReservaMapper::toDomain);
        } catch (Exception e) {
            registrar("buscarPorId", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta una reserva validando sus datos y estado.
     *
     * @param dato reserva a insertar
     * @return identificador generado, o 0 si los datos no son válidos o falla la inserción
     */
    public int insertar(Reserva dato) {
        if (!validar(dato)) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            ReservaEntity entidad = ReservaMapper.toEntity(dato);
            em.persist(entidad);
            em.flush();
            tx.commit();
            return entidad.getIdReserva();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            registrar("insertar", ex);
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza los datos de una reserva existente.
     *
     * @param dato reserva con el identificador y los nuevos valores
     * @return 1 si se actualizó, 0 en caso contrario
     */
    public int actualizar(Reserva dato) {
        if (dato == null || dato.getIdReserva() == null || !validar(dato)) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            ReservaEntity entidad = em.find(ReservaEntity.class, dato.getIdReserva());

            if (entidad == null) {
                tx.rollback();
                return 0;
            }

            ReservaEntity actualizado = ReservaMapper.toEntity(dato);
            entidad.setIdSocio(actualizado.getIdSocio());
            entidad.setIdLibro(actualizado.getIdLibro());
            entidad.setFechaReserva(actualizado.getFechaReserva());
            entidad.setEstado(actualizado.getEstado());

            tx.commit();
            return 1;
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            registrar("actualizar", ex);
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una reserva por identificador.
     *
     * @param id identificador de la reserva
     * @return 1 si se eliminó, 0 en caso contrario
     */
    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            ReservaEntity entidad = em.find(ReservaEntity.class, id);

            if (entidad == null) {
                tx.rollback();
                return 0;
            }

            em.remove(entidad);
            tx.commit();
            return 1;
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            registrar("eliminar", ex);
            return 0;
        } finally {
            em.close();
        }
    }

    private boolean validar(Reserva dato) {
        return dato != null
                && dato.getIdSocio() != null
                && dato.getIdLibro() != null
                && dato.getFechaReserva() != null
                && dato.getEstado() != null
                && (dato.getEstado().equals("PENDIENTE")
                || dato.getEstado().equals("ATENDIDA")
                || dato.getEstado().equals("CANCELADA"));
    }

    /**
     * Lista las reservas pendientes ordenadas por fecha.
     *
     * @return lista de reservas pendientes
     */
    public List<Reserva> listarPendientes() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT r FROM ReservaEntity r "
                                    + "WHERE r.estado = 'PENDIENTE' "
                                    + "ORDER BY r.fechaReserva, r.idReserva",
                            ReservaEntity.class)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listarPendientes", e);
            return List.of();
        }
    }

    /**
     * Busca las reservas realizadas por un socio.
     *
     * @param idSocio identificador del socio
     * @return lista de reservas del socio
     */
    public List<Reserva> buscarPorSocio(int idSocio) {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT r FROM ReservaEntity r "
                                    + "WHERE r.idSocio = :idSocio "
                                    + "ORDER BY r.fechaReserva, r.idReserva",
                            ReservaEntity.class)
                    .setParameter("idSocio", idSocio)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("buscarPorSocio", e);
            return List.of();
        }
    }

    /**
     * Lista las reservas asociadas a un libro.
     *
     * @param idLibro identificador del libro
     * @return lista de reservas del libro
     */
    public List<Reserva> listarPorLibro(int idLibro) {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT r FROM ReservaEntity r "
                                    + "WHERE r.idLibro = :idLibro "
                                    + "ORDER BY r.fechaReserva, r.idReserva",
                            ReservaEntity.class)
                    .setParameter("idLibro", idLibro)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listarPorLibro", e);
            return List.of();
        }
    }

    /**
     * Cuenta las reservas pendientes registradas.
     *
     * @return número de reservas pendientes
     */
    public long contarReservasPendientes() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT COUNT(r) FROM ReservaEntity r "
                                    + "WHERE r.estado = 'PENDIENTE'",
                            Long.class)
                    .getSingleResult();
        } catch (Exception e) {
            registrar("contarReservasPendientes", e);
            return 0L;
        }
    }

    /**
     * Devuelve reservas junto con los datos del socio y del libro.
     *
     * @return lista de arrays con reserva, socio y libro
     */
    public List<Object[]> reservasConDetalles() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT r, s, l "
                                    + "FROM ReservaEntity r "
                                    + "JOIN SocioEntity s ON s.idSocio = r.idSocio "
                                    + "JOIN LibroEntity l ON l.idLibro = r.idLibro "
                                    + "ORDER BY r.fechaReserva, r.idReserva",
                            Object[].class)
                    .getResultList();
        } catch (Exception e) {
            registrar("reservasConDetalles", e);
            return List.of();
        }
    }

    /**
     * Devuelve la reserva pendiente más antigua de cada libro.
     *
     * @return lista de arrays con reserva, socio y libro
     */
    public List<Object[]> prioridadReservas() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT r, s, l "
                                    + "FROM ReservaEntity r "
                                    + "JOIN SocioEntity s ON s.idSocio = r.idSocio "
                                    + "JOIN LibroEntity l ON l.idLibro = r.idLibro "
                                    + "WHERE r.estado = 'PENDIENTE' "
                                    + "AND r.fechaReserva = ("
                                    + "SELECT MIN(r2.fechaReserva) "
                                    + "FROM ReservaEntity r2 "
                                    + "WHERE r2.idLibro = r.idLibro "
                                    + "AND r2.estado = 'PENDIENTE') "
                                    + "ORDER BY r.idLibro, r.fechaReserva, r.idReserva",
                            Object[].class)
                    .getResultList();
        } catch (Exception e) {
            registrar("prioridadReservas", e);
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