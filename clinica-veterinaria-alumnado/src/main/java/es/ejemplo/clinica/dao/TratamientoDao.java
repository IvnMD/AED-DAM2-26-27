package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Tratamiento;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TratamientoDao implements ITratamientoDao {

    private final String url;

    public TratamientoDao(String url) {
        this.url = url;
    }

<<<<<<< Updated upstream
    private Tratamiento mapTratamiento(ResultSet rs) throws SQLException {
        return new Tratamiento(
                rs.getInt("id"),
                rs.getInt("id_consulta"),
                rs.getString("descripcion"),
                rs.getInt("cantidad"),
                rs.getBigDecimal("precio_unitario")
        );
=======
    private Tratamiento mapear(ResultSet rs) throws SQLException {
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setId(rs.getInt("id"));
        tratamiento.setIdConsulta(rs.getInt("id_consulta"));
        tratamiento.setDescripcion(rs.getString("descripcion"));
        tratamiento.setCantidad(rs.getInt("cantidad"));
        tratamiento.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        return tratamiento;
>>>>>>> Stashed changes
    }

    @Override
    public List<Tratamiento> listar() {
        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos ORDER BY id";
<<<<<<< Updated upstream
        List<Tratamiento> tratamientos = new ArrayList<>();
=======
        List<Tratamiento> lista = new ArrayList<>();
>>>>>>> Stashed changes

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
<<<<<<< Updated upstream
                tratamientos.add(mapTratamiento(rs));
            }

            return tratamientos;

        } catch (SQLException e) {
            throw new DaoException("Error al listar tratamientos", e);
        }
    }

=======
                lista.add(mapear(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al listar tratamientos", e);
        }
    }

>>>>>>> Stashed changes
    @Override
    public Optional<Tratamiento> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
<<<<<<< Updated upstream
                if (rs.next()) {
                    return Optional.of(mapTratamiento(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar tratamiento", e);
=======
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new DaoException("Error al buscar el tratamiento " + id, e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public int insertar(Tratamiento entidad) {
        String sql = "INSERT INTO tratamientos (id_consulta, descripcion, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entidad.getIdConsulta());
            ps.setString(2, entidad.getDescripcion());
            ps.setInt(3, entidad.getCantidad());
            ps.setBigDecimal(4, entidad.getPrecioUnitario());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entidad.setId(id);
                    return id;
                }
<<<<<<< Updated upstream
            }

            throw new DaoException("No se pudo obtener el identificador del tratamiento insertado", null);

        } catch (SQLException e) {
            throw new DaoException("Error al insertar tratamiento", e);
=======
                throw new DaoException("No se pudo obtener el identificador del tratamiento insertado", null);
            }

        } catch (SQLException e) {
            throw new DaoException("Error al insertar el tratamiento", e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public int actualizar(Tratamiento entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException("El tratamiento debe tener un identificador");
        }

        String sql = "UPDATE tratamientos SET id_consulta = ?, descripcion = ?, cantidad = ?, precio_unitario = ? WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, entidad.getIdConsulta());
            ps.setString(2, entidad.getDescripcion());
            ps.setInt(3, entidad.getCantidad());
            ps.setBigDecimal(4, entidad.getPrecioUnitario());
            ps.setInt(5, entidad.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
<<<<<<< Updated upstream
            throw new DaoException("Error al actualizar tratamiento", e);
=======
            throw new DaoException("Error al actualizar el tratamiento " + entidad.getId(), e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }

        String sql = "DELETE FROM tratamientos WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
<<<<<<< Updated upstream
            throw new DaoException("Error al eliminar tratamiento", e);
=======
            throw new DaoException("Error al eliminar el tratamiento " + id, e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public List<Tratamiento> listarPorConsulta(Integer idConsulta) {
        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos WHERE id_consulta = ? ORDER BY id";
<<<<<<< Updated upstream
        List<Tratamiento> tratamientos = new ArrayList<>();
=======
        List<Tratamiento> lista = new ArrayList<>();
>>>>>>> Stashed changes

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
<<<<<<< Updated upstream
                    tratamientos.add(mapTratamiento(rs));
                }
            }

            return tratamientos;

        } catch (SQLException e) {
            throw new DaoException("Error al listar tratamientos por consulta", e);
=======
                    lista.add(mapear(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al listar tratamientos de la consulta " + idConsulta, e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
<<<<<<< Updated upstream
        if (idConsulta == null) {
            return BigDecimal.ZERO;
        }

=======
>>>>>>> Stashed changes
        String sql = "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) AS total FROM tratamientos WHERE id_consulta = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
<<<<<<< Updated upstream
                if (rs.next() && rs.getBigDecimal("total") != null) {
                    return rs.getBigDecimal("total");
                }
            }

            return BigDecimal.ZERO;

        } catch (SQLException e) {
            throw new DaoException("Error al calcular el coste de los tratamientos", e);
=======
                return rs.next() ? rs.getBigDecimal("total") : BigDecimal.ZERO;
            }

        } catch (SQLException e) {
            throw new DaoException("Error al calcular el coste de la consulta " + idConsulta, e);
>>>>>>> Stashed changes
        }
    }
}