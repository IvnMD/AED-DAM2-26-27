package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import es.ejemplo.taller.modelo.Mecanico;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class MecanicoDao extends AbstractDao<Mecanico> {

    public MecanicoDao(String url) {
        super(url);
    }

    @Override
    protected Optional<Mecanico> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Mecanico(
                    rs.getInt("id_mecanico"),
                    rs.getString("nombre"),
                    rs.getString("especialidad"),
                    rs.getBigDecimal("tarifa_hora")
            ));
        } catch (SQLException e) {
            registrar("mapRsToClass mecanico", e);
            return Optional.empty();
        }
    }

    public List<Mecanico> listar() {
        String sql = "SELECT * FROM mecanicos";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar mecanicos", e);
            return List.of();
        }
    }

    public Optional<Mecanico> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM mecanicos WHERE id_mecanico = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorId mecanico", e);
            return Optional.empty();
        }
    }

    public int insertar(Mecanico dato) {

        String sql =" INSERT INTO mecanicos (nombre, especialidad, tarifa_hora) VALUES (?, ?, ?)";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException | RuntimeException e) {
            registrar("insertar mecanico", e);
            return 0;
        }
    }

    public int actualizar(Mecanico dato) {


        String sql = "UPDATE mecanicos SET nombre = ?,especialidad = ?,tarifa_hora = ? WHERE id_mecanico = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            ps.setInt(4, dato.getIdMecanico());

            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("actualizar mecanico", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }

        String sql = "DELETE FROM mecanicos WHERE id_mecanico = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("eliminar mecanico", e);
            return 0;
        }
    }

    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            return List.of();
        }

        String sql = "SELECT * FROM mecanicos WHERE especialidad = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, especialidad);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorEspecialidad mecanico", e);
            return List.of();
        }
    }
}