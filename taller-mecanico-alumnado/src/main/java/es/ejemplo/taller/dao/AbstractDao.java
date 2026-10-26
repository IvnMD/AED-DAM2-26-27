package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractDao<T> extends ConexionBD {

    private static final Logger LOG = Logger.getLogger(AbstractDao.class.getName());

    protected AbstractDao(String url) {
        super(url);
    }

    /** Cada DAO convierte una fila del ResultSet en su objeto. */
    protected abstract Optional<T> mapRsToClass(ResultSet rs);

    /** Ejecuta una consulta y devuelve una lista. Nunca devuelve null. */
    protected List<T> consultar(PreparedStatement ps) {
        if (ps == null) {
            return List.of();
        }

        try (ResultSet rs = ps.executeQuery()) {
            List<T> resultado = new ArrayList<>();

            while (rs.next()) {
                Optional<T> entidad = mapRsToClass(rs);

                if (entidad.isEmpty()) {
                    return List.of();
                }

                resultado.add(entidad.get());
            }

            return resultado;
        } catch (SQLException | RuntimeException e) {
            registrar("consultar", e);
            return List.of();
        }
    }

    /** Ejecuta una consulta que devuelve cero o un registro. */
    protected Optional<T> consultarUno(PreparedStatement ps) {
        if (ps == null) {
            return Optional.empty();
        }

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapRsToClass(rs) : Optional.empty();
        } catch (SQLException | RuntimeException e) {
            registrar("consultarUno", e);
            return Optional.empty();
        }
    }

    /** Ejecuta INSERT, UPDATE o DELETE. Devuelve true si afecta a alguna fila. */
    protected boolean ejecutar(PreparedStatement ps) {
        if (ps == null) {
            return false;
        }

        try {
            return ps.executeUpdate() > 0;
        } catch (SQLException | RuntimeException e) {
            registrar("ejecutar", e);
            return false;
        }
    }

    /** Ejecuta una consulta que devuelve un número, como COUNT o SUM. */
    protected int consultarEntero(PreparedStatement ps) {
        if (ps == null) {
            return 0;
        }

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException | RuntimeException e) {
            registrar("consultarEntero", e);
            return 0;
        }
    }

    protected void registrar(String operacion, Exception e) {
        LOG.log(Level.WARNING, "Fallo JDBC: " + operacion, e);
    }
}