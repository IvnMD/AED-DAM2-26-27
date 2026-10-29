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

    private Tratamiento mapTratamiento(ResultSet rs) throws SQLException {
        return new Tratamiento(
                rs.getInt("id"),
                rs.getInt("id_consulta"),
                rs.getString("descripcion"),
                rs.getInt("cantidad"),
                rs.getBigDecimal("precio_unitario")
        );
    }

    @Override
    public List<Tratamiento> listar() {
        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos ORDER BY id";
        List<Tratamiento> tratamientos = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tratamientos.add(mapTratamiento(rs));
            }

            return tratamientos;

        } catch (SQLException e) {
            throw new DaoException("Error al listar tratamientos", e);
        }
    }

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
                if (rs.next()) {
                    return Optional.of(mapTratamiento(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar tratamiento", e);
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
            }

            throw new DaoException("No se pudo obtener el identificador del tratamiento insertado", null);

        } catch (SQLException e) {
            throw new DaoException("Error al insertar tratamiento", e);
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
            throw new DaoException("Error al actualizar tratamiento", e);
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
            throw new DaoException("Error al eliminar tratamiento", e);
        }
    }

    @Override
    public List<Tratamiento> listarPorConsulta(Integer idConsulta) {
        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario FROM tratamientos WHERE id_consulta = ? ORDER BY id";
        List<Tratamiento> tratamientos = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tratamientos.add(mapTratamiento(rs));
                }
            }

            return tratamientos;

        } catch (SQLException e) {
            throw new DaoException("Error al listar tratamientos por consulta", e);
        }
    }

    @Override
    public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
        if (idConsulta == null) {
            return BigDecimal.ZERO;
        }

        String sql = "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) AS total FROM tratamientos WHERE id_consulta = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getBigDecimal("total") != null) {
                    return rs.getBigDecimal("total");
                }
            }

            return BigDecimal.ZERO;

        } catch (SQLException e) {
            throw new DaoException("Error al calcular el coste de los tratamientos", e);
        }
    }
}