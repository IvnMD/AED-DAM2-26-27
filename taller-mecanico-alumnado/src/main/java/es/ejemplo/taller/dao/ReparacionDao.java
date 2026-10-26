package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.Reparacion;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class ReparacionDao extends AbstractDao<Reparacion> {

    public ReparacionDao(String url) {
        super(url);
    }

    @Override
    protected Optional<Reparacion> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Reparacion(
                    rs.getInt("id_reparacion"),
                    rs.getString("matricula"),
                    rs.getInt("id_mecanico"),
                    rs.getString("descripcion"),
                    rs.getString("fecha"),
                    rs.getBigDecimal("horas_trabajo"),
                    rs.getString("estado")
            ));
        } catch (SQLException e) {
            registrar("mapRsToClass reparacion", e);
            return Optional.empty();
        }
    }

    public List<Reparacion> listar() {
        String sql = "SELECT * FROM reparaciones";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar reparaciones", e);
            return List.of();
        }
    }

    public Optional<Reparacion> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM reparaciones WHERE id_reparacion = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorId reparacion", e);
            return Optional.empty();
        }
    }

    public int insertar(Reparacion dato) {

        String sql = "INSERT INTO reparaciones(matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setBigDecimal(5, dato.getHorasTrabajo());
            ps.setString(6, dato.getEstado());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException | RuntimeException e) {
            registrar("insertar reparacion", e);
            return 0;
        }
    }

    public int actualizar(Reparacion dato) {

        String sql = "UPDATE reparaciones SET matricula = ?, id_mecanico = ?, descripcion = ?, fecha = ?, horas_trabajo = ?, estado = ? WHERE id_reparacion = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setBigDecimal(5, dato.getHorasTrabajo());
            ps.setString(6, dato.getEstado());
            ps.setInt(7, dato.getIdReparacion());

            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("actualizar reparacion", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }

        String sql = "DELETE FROM reparaciones WHERE id_reparacion = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("eliminar reparacion", e);
            return 0;
        }
    }

    public List<Reparacion> buscarPorVehiculo(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return List.of();
        }

        String sql = "SELECT * FROM reparaciones WHERE matricula = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, matricula);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorVehiculo reparacion", e);
            return List.of();
        }
    }

    public List<Reparacion> buscarPorMecanico(Integer idMecanico) {
        if (idMecanico == null) {
            return List.of();
        }

        String sql = "SELECT * FROM reparaciones WHERE id_mecanico = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMecanico);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorMecanico reparacion", e);
            return List.of();
        }
    }

    public List<Reparacion> listarPendientes() {
        String sql = "SELECT * FROM reparaciones WHERE estado = 'PENDIENTE'";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listarPendientes reparacion", e);
            return List.of();
        }
    }

}
