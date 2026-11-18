package es.ejemplo.biblioteca.dao;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ejemplo.biblioteca.modelo.Socio;
import es.ejemplo.biblioteca.orm.entity.SocioEntity;
import es.ejemplo.biblioteca.orm.mapper.SocioMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

/**
 * DAO responsable de las operaciones de persistencia y consultas sobre socios.
 */
public class SocioDao implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(SocioDao.class.getName());

    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea un DAO que gestiona su propio EntityManagerFactory.
     *
     * @param url URL JDBC de la base de datos SQLite
     */
    public SocioDao(String url) {
        this.managerFactory = es.ejemplo.biblioteca.orm.config.OrmFactory.crear(url);
        this.ownFactory = true;
    }

    /**
     * Crea un DAO que reutiliza un EntityManagerFactory existente.
     *
     * @param managerFactory fábrica compartida de gestores de entidades
     */
    public SocioDao(EntityManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
        this.ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Obtiene todos los socios ordenados por identificador.
     *
     * @return lista de socios; vacía si no hay datos o se produce un error
     */
    public List<Socio> listar() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM SocioEntity s ORDER BY s.idSocio",
                            SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listar", e);
            return List.of();
        }
    }

    /**
     * Busca un socio por su identificador.
     *
     * @param id identificador del socio
     * @return socio encontrado u Optional vacío
     */
    public Optional<Socio> buscarPorId(Integer id) {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SocioEntity.class, id))
                    .map(SocioMapper::toDomain);
        } catch (Exception e) {
            registrar("buscarPorId", e);
            return Optional.empty();
        }
    }

    /**
     * Inserta un socio validando sus datos obligatorios.
     *
     * @param dato socio a insertar
     * @return identificador generado, o 0 si los datos no son válidos o falla la inserción
     */
    public int insertar(Socio dato) {
        if (!validar(dato)) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            SocioEntity entidad = SocioMapper.toEntity(dato);
            em.persist(entidad);
            em.flush();
            tx.commit();
            return entidad.getIdSocio();
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
     * Actualiza los datos de un socio existente.
     *
     * @param dato socio con el identificador y los nuevos valores
     * @return 1 si se actualizó, 0 en caso contrario
     */
    public int actualizar(Socio dato) {
        if (dato == null || dato.getIdSocio() == null || !validar(dato)) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            SocioEntity entidad = em.find(SocioEntity.class, dato.getIdSocio());

            if (entidad == null) {
                tx.rollback();
                return 0;
            }

            SocioEntity actualizado = SocioMapper.toEntity(dato);
            entidad.setNombre(actualizado.getNombre());
            entidad.setEmail(actualizado.getEmail());
            entidad.setTelefono(actualizado.getTelefono());
            entidad.setFechaAlta(actualizado.getFechaAlta());
            entidad.setActivo(actualizado.getActivo());

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
     * Elimina un socio por identificador.
     *
     * @param id identificador del socio
     * @return 1 si se eliminó, 0 si no existe o tiene préstamos asociados
     */
    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }

        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            SocioEntity entidad = em.find(SocioEntity.class, id);

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

    private boolean validar(Socio dato) {
        return dato != null
                && dato.getNombre() != null
                && !dato.getNombre().isBlank()
                && dato.getEmail() != null
                && !dato.getEmail().isBlank()
                && dato.getFechaAlta() != null
                && dato.getActivo() != null;
    }

    /**
     * Lista los socios activos ordenados por fecha de alta descendente.
     *
     * @return lista de socios activos
     */
    public List<Socio> listarActivos() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM SocioEntity s "
                                    + "WHERE s.activo = true "
                                    + "ORDER BY s.fechaAlta DESC, s.idSocio",
                            SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listarActivos", e);
            return List.of();
        }
    }

    /**
     * Busca un socio por su correo electrónico.
     *
     * @param email correo electrónico del socio
     * @return socio encontrado u Optional vacío
     */
    public Optional<Socio> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM SocioEntity s WHERE s.email = :email",
                            SocioEntity.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .map(SocioMapper::toDomain);
        } catch (Exception e) {
            registrar("buscarPorEmail", e);
            return Optional.empty();
        }
    }

    /**
     * Lista los socios que tienen algún préstamo vencido en la fecha indicada.
     *
     * @param fecha fecha de referencia en formato YYYY-MM-DD
     * @return lista de socios con préstamos vencidos
     */
    public List<Socio> listarConPrestamosVencidos(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return List.of();
        }

        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT DISTINCT s FROM SocioEntity s "
                                    + "JOIN PrestamoEntity p ON p.idSocio = s.idSocio "
                                    + "WHERE p.fechaDevolucion IS NULL "
                                    + "AND p.fechaLimite < :fecha "
                                    + "ORDER BY s.idSocio",
                            SocioEntity.class)
                    .setParameter("fecha", fecha)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("listarConPrestamosVencidos", e);
            return List.of();
        }
    }

    /**
     * Lista los socios que no tienen ningún préstamo registrado.
     *
     * @return lista de socios sin préstamos
     */
    public List<Socio> sociosSinPrestamos() {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM SocioEntity s "
                                    + "WHERE NOT EXISTS ("
                                    + "SELECT p FROM PrestamoEntity p "
                                    + "WHERE p.idSocio = s.idSocio) "
                                    + "ORDER BY s.idSocio",
                            SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (Exception e) {
            registrar("sociosSinPrestamos", e);
            return List.of();
        }
    }

    /**
     * Obtiene el ranking de socios con más préstamos.
     *
     * @param limite número máximo de socios a devolver
     * @return lista de arrays con socio y número de préstamos
     */
    public List<Object[]> rankingSocios(int limite) {
        if (limite <= 0) {
            return List.of();
        }

        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT s, COUNT(p.idPrestamo) "
                                    + "FROM SocioEntity s "
                                    + "LEFT JOIN PrestamoEntity p ON p.idSocio = s.idSocio "
                                    + "GROUP BY s.idSocio, s.nombre "
                                    + "ORDER BY COUNT(p.idPrestamo) DESC, s.nombre",
                            Object[].class)
                    .setMaxResults(limite)
                    .getResultList();
        } catch (Exception e) {
            registrar("rankingSocios", e);
            return List.of();
        }
    }

    /**
     * Obtiene el historial de préstamos de un socio.
     *
     * @param idSocio identificador del socio
     * @return lista de arrays con préstamo, libro y estado de devolución
     */
    public List<Object[]> historialSocio(int idSocio) {
        try (EntityManager em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT p, l, "
                                    + "CASE "
                                    + "WHEN p.fechaDevolucion IS NULL AND p.fechaLimite < CURRENT_DATE "
                                    + "THEN 'VENCIDO' "
                                    + "WHEN p.fechaDevolucion IS NOT NULL AND p.fechaDevolucion > p.fechaLimite "
                                    + "THEN 'DEVUELTO CON RETRASO' "
                                    + "WHEN p.fechaDevolucion IS NOT NULL "
                                    + "THEN 'DEVUELTO' "
                                    + "ELSE 'ACTIVO' "
                                    + "END "
                                    + "FROM PrestamoEntity p "
                                    + "JOIN LibroEntity l ON l.idLibro = p.idLibro "
                                    + "WHERE p.idSocio = :idSocio "
                                    + "ORDER BY p.fechaPrestamo, p.idPrestamo",
                            Object[].class)
                    .setParameter("idSocio", idSocio)
                    .getResultList();
        } catch (Exception e) {
            registrar("historialSocio", e);
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