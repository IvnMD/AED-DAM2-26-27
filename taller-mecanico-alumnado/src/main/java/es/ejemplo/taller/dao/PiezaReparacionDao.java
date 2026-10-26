package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.PiezaReparacion;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class PiezaReparacionDao extends AbstractDao<PiezaReparacion> {

    public PiezaReparacionDao(String url) {
        super(url);
    }

    @Override
    protected Optional<PiezaReparacion> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new PiezaReparacion(
                    rs.getInt("id_pieza"),
                    rs.getInt("id_reparacion"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getBigDecimal("precio_unitario")
            ));
        } catch (SQLException e) {
            registrar("mapRsToClass pieza", e);
            return Optional.empty();
        }
    }

    public List<PiezaReparacion> listar() {
        String sql = "SELECT * FROM piezas_reparacion";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar piezas", e);
            return List.of();
        }
    }

    public Optional<PiezaReparacion> buscarPorId(Integer id) {


        String sql = "SELECT * FROM piezas_reparacion WHERE id_pieza = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorId pieza", e);
            return Optional.empty();
        }
    }

    public int insertar(PiezaReparacion dato) {


        String sql = " INSERT INTO piezas_reparacion (id_reparacion, nombre, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException | RuntimeException e) {
            registrar("insertar pieza", e);
            return 0;
        }
    }

    public int actualizar(PiezaReparacion dato) {

        String sql = " UPDATE piezas_reparacion SET id_reparacion = ?, nombre = ?, cantidad = ?, precio_unitario = ? WHERE id_pieza = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());
            ps.setInt(5, dato.getIdPieza());

            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("actualizar pieza", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {


        String sql = "DELETE FROM piezas_reparacion WHERE id_pieza = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("eliminar pieza", e);
            return 0;
        }
    }

    public List<PiezaReparacion> listarPorReparacion(Integer idReparacion) {

        String sql = "SELECT * FROM piezas_reparacion WHERE id_reparacion = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idReparacion);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listarPorReparacion pieza", e);
            return List.of();
        }
    }

    public BigDecimal calcularCostePiezas(Integer idReparacion) {


        String sql = "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) FROM piezas_reparacion WHERE id_reparacion = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idReparacion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal coste = rs.getBigDecimal(1);
                    return coste == null ? BigDecimal.ZERO : coste;
                }

                return BigDecimal.ZERO;
            }
        } catch (SQLException | RuntimeException e) {
            registrar("calcularCostePiezas", e);
            return BigDecimal.ZERO;
        }
    }
}