package es.ejemplo.biblioteca.dao;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ejemplo.biblioteca.modelo.Prestamo;
import es.ejemplo.biblioteca.orm.entity.PrestamoEntity;
import es.ejemplo.biblioteca.orm.mapper.PrestamoMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

/**
 * DAO responsable de las operaciones de persistencia y consultas sobre préstamos.
 */
public class PrestamoDao implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(PrestamoDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea un DAO que gestiona su propio EntityManagerFactory.
     *
     * @param url URL JDBC de la base de datos SQLite
     */
    public PrestamoDao(String url) {
        this.managerFactory = es.ejemplo.biblioteca.orm.config.OrmFactory.crear(url);
        this.ownFactory = true;
    }

    /**
     * Crea un DAO que reutiliza un EntityManagerFactory existente.
     *
     * @param managerFactory fábrica compartida de gestores de entidades
     */
    public PrestamoDao(EntityManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
        this.ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Obtiene todos los préstamos ordenados por identificador.
     *
     * @return lista de préstamos; vacía si no hay datos o se produce un error
     */
    public List<Prestamo> listar() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM PrestamoEntity p ORDER BY p.idPrestamo",
                            PrestamoEntity.class)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    /**
     * Busca un préstamo por su identificador.
     *
     * @param id identificador del préstamo
     * @return préstamo encontrado u Optional vacío
     */
    public Optional<Prestamo> buscarPorId(Integer id) {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(PrestamoEntity.class, id))
                    .map(PrestamoMapper::toDomain);
        } catch (Exception e) {
            registrar("buscarPorId", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta un préstamo validando sus fechas.
     *
     * @param dato préstamo a insertar
     * @return identificador generado, o 0 si los datos no son válidos o falla la inserción
     */
    public int insertar(Prestamo dato) {
        if (!validar(dato)) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            PrestamoEntity entidad = PrestamoMapper.toEntity(dato);
            em.persist(entidad);
            em.flush();
            tx.commit();
            return entidad.getIdPrestamo();
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
     * Actualiza los datos de un préstamo existente.
     *
     * @param dato préstamo con el identificador y los nuevos valores
     * @return 1 si se actualizó, 0 en caso contrario
     */
    public int actualizar(Prestamo dato) {
        if (dato == null || dato.getIdPrestamo() == null || !validar(dato)) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            PrestamoEntity entidad = em.find(PrestamoEntity.class, dato.getIdPrestamo());

            if (entidad == null) {
                tx.rollback();
                return 0;
            }

            PrestamoEntity actualizado = PrestamoMapper.toEntity(dato);
            entidad.setIdSocio(actualizado.getIdSocio());
            entidad.setIdLibro(actualizado.getIdLibro());
            entidad.setFechaPrestamo(actualizado.getFechaPrestamo());
            entidad.setFechaLimite(actualizado.getFechaLimite());
            entidad.setFechaDevolucion(actualizado.getFechaDevolucion());

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
     * Elimina un préstamo por identificador.
     *
     * @param id identificador del préstamo
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
            PrestamoEntity entidad = em.find(PrestamoEntity.class, id);

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

    private boolean validar(Prestamo dato) {
        return dato != null
                && dato.getIdSocio() != null
                && dato.getIdLibro() != null
                && dato.getFechaPrestamo() != null
                && dato.getFechaLimite() != null
                && dato.getFechaLimite().compareTo(dato.getFechaPrestamo()) >= 0
                && (dato.getFechaDevolucion() == null
                || dato.getFechaDevolucion().compareTo(dato.getFechaPrestamo()) >= 0);
    }

    /**
     * Lista los préstamos realizados por un socio.
     *
     * @param idSocio identificador del socio
     * @return lista de préstamos del socio
     */
    public List<Prestamo> listarPorSocio(int idSocio) {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM PrestamoEntity p "
                                    + "WHERE p.idSocio = :idSocio "
                                    + "ORDER BY p.fechaPrestamo, p.idPrestamo",
                            PrestamoEntity.class)
                    .setParameter("idSocio", idSocio)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listarPorSocio", e);
            return List.of();
        }
    }

    /**
     * Lista los préstamos que todavía no han sido devueltos.
     *
     * @return lista de préstamos activos
     */
    public List<Prestamo> listarActivos() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM PrestamoEntity p "
                                    + "WHERE p.fechaDevolucion IS NULL "
                                    + "ORDER BY p.fechaPrestamo, p.idPrestamo",
                            PrestamoEntity.class)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listarActivos", e);
            return List.of();
        }
    }

    /**
     * Lista los préstamos activos cuya fecha límite es anterior a la fecha indicada.
     *
     * @param fecha fecha de referencia en formato YYYY-MM-DD
     * @return lista de préstamos vencidos
     */
    public List<Prestamo> listarVencidos(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return List.of();
        }

        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM PrestamoEntity p "
                                    + "WHERE p.fechaDevolucion IS NULL "
                                    + "AND p.fechaLimite < :fecha "
                                    + "ORDER BY p.fechaLimite, p.idPrestamo",
                            PrestamoEntity.class)
                    .setParameter("fecha", fecha)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listarVencidos", e);
            return List.of();
        }
    }

    /**
     * Cuenta el número total de préstamos históricos de un libro.
     *
     * @param idLibro identificador del libro
     * @return número de préstamos del libro
     */
    public long contarPrestamosPorLibro(int idLibro) {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT COUNT(p) FROM PrestamoEntity p "
                                    + "WHERE p.idLibro = :idLibro",
                            Long.class)
                    .setParameter("idLibro", idLibro)
                    .getSingleResult();
        } catch (Exception e) {
            registrar("contarPrestamosPorLibro", e);
            return 0L;
        }
    }

    /**
     * Devuelve préstamos junto con los datos del socio y del libro asociados.
     *
     * @return lista de arrays con préstamo, socio y libro
     */
    public List<Object[]> prestamosConSocioYLibro() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT p, s, l "
                                    + "FROM PrestamoEntity p "
                                    + "JOIN SocioEntity s ON s.idSocio = p.idSocio "
                                    + "JOIN LibroEntity l ON l.idLibro = p.idLibro "
                                    + "ORDER BY p.fechaPrestamo, p.idPrestamo",
                            Object[].class)
                    .getResultList();
        } catch (Exception e) {
            registrar("prestamosConSocioYLibro", e);
            return List.of();
        }
    }

    /**
     * Obtiene el ranking de libros más prestados durante un mes.
     *
     * @param anioMes mes en formato YYYY-MM
     * @param limite  número máximo de resultados
     * @return lista de arrays con identificador, título y número de préstamos
     */
    public List<Object[]> rankingMensual(String anioMes, int limite) {
        if (anioMes == null || anioMes.isBlank() || limite <= 0) {
            return List.of();
        }

        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT l.idLibro, l.titulo, COUNT(p.idPrestamo) "
                                    + "FROM PrestamoEntity p "
                                    + "JOIN LibroEntity l ON l.idLibro = p.idLibro "
                                    + "WHERE SUBSTRING(p.fechaPrestamo, 1, 7) = :anioMes "
                                    + "GROUP BY l.idLibro, l.titulo "
                                    + "ORDER BY COUNT(p.idPrestamo) DESC, l.titulo",
                            Object[].class)
                    .setParameter("anioMes", anioMes)
                    .setMaxResults(limite)
                    .getResultList();
        } catch (Exception e) {
            registrar("rankingMensual", e);
            return List.of();
        }
    }

    /**
     * Registra un préstamo solo si el socio está activo y queda algún ejemplar disponible.
     * La comprobación y la inserción se realizan de forma atómica.
     *
     * @param idSocio identificador del socio
     * @param idLibro identificador del libro
     * @param inicio  fecha de préstamo en formato YYYY-MM-DD
     * @param limite  fecha límite en formato YYYY-MM-DD
     * @return identificador generado, o 0 si no es posible realizar el préstamo
     */
    public int registrarPrestamo(int idSocio, int idLibro, String inicio, String limite) {
        if (inicio == null || limite == null || limite.compareTo(inicio) < 0) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            int filas = em.createNativeQuery("""
                            INSERT INTO prestamos
                                (id_socio, id_libro, fecha_prestamo, fecha_limite, fecha_devolucion)
                            SELECT :idSocio, :idLibro, :inicio, :limite, NULL
                            WHERE EXISTS (
                                SELECT 1
                                FROM socios
                                WHERE id_socio = :idSocio
                                  AND activo = 1
                            )
                            AND EXISTS (
                                SELECT 1
                                FROM libros
                                WHERE id_libro = :idLibro
                            )
                            AND (
                                SELECT ejemplares
                                FROM libros
                                WHERE id_libro = :idLibro
                            ) > (
                                SELECT COUNT(*)
                                FROM prestamos
                                WHERE id_libro = :idLibro
                                  AND fecha_devolucion IS NULL
                            )
                            """)
                    .setParameter("idSocio", idSocio)
                    .setParameter("idLibro", idLibro)
                    .setParameter("inicio", inicio)
                    .setParameter("limite", limite)
                    .executeUpdate();

            if (filas == 0) {
                tx.rollback();
                return 0;
            }

            Object identificador = em
                    .createNativeQuery("SELECT last_insert_rowid()")
                    .getSingleResult();

            tx.commit();
            return ((Number) identificador).intValue();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            registrar("registrarPrestamo", ex);
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Registra la devolución de un préstamo activo.
     *
     * @param id    identificador del préstamo
     * @param fecha fecha de devolución en formato YYYY-MM-DD
     * @return 1 si se registró la devolución, 0 en caso contrario
     */
    public int devolver(int id, String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            int filas = em.createQuery(
                            "UPDATE PrestamoEntity p "
                                    + "SET p.fechaDevolucion = :fecha "
                                    + "WHERE p.idPrestamo = :id "
                                    + "AND p.fechaDevolucion IS NULL "
                                    + "AND p.fechaPrestamo <= :fecha")
                    .setParameter("fecha", fecha)
                    .setParameter("id", id)
                    .executeUpdate();

            tx.commit();
            return filas;
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            registrar("devolver", ex);
            return 0;
        } finally {
            em.close();
        }
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) {
            managerFactory.close();
        }
    }
}