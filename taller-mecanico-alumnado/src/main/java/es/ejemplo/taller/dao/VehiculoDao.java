package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Mecanico;
import es.ejemplo.taller.modelo.Vehiculo;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class VehiculoDao extends AbstractDao<Vehiculo> {

    public VehiculoDao(String url) {
        super(url);
    }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Vehiculo(
                    rs.getString("matricula"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("anio"),
                    rs.getInt("id_cliente")
            ));
        } catch (SQLException e) {
            registrar("mapRsToClass vehiculo", e);
            return Optional.empty();
        }
    }

    public List<Vehiculo> listar() {
        String sql = "SELECT * FROM vehiculos";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar vehiculos", e);
            return List.of();
        }
    }

    public Optional<Vehiculo> buscarPorId(String matricula) {

        String sql = "SELECT * FROM vehiculos WHERE matricula = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, matricula);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorId vehiculo", e);
            return Optional.empty();
        }
    }

    public int insertar(Vehiculo dato) {

        String sql = "INSERT INTO vehiculos (matricula, marca, modelo, anio, id_cliente) VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dato.getMatricula());
            ps.setString(2, dato.getMarca());
            ps.setString(3, dato.getModelo());
            ps.setInt(4, dato.getAnio());
            ps.setInt(5, dato.getIdCliente());

            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("insertar vehiculo", e);
            return 0;
        }
    }

    public int actualizar(Vehiculo dato) {

        String sql = "UPDATE vehiculos SET marca = ?, modelo = ?, anio = ?, id_cliente = ? WHERE matricula = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dato.getMarca());
            ps.setString(2, dato.getModelo());
            ps.setInt(3, dato.getAnio());
            ps.setInt(4, dato.getIdCliente());
            ps.setString(5, dato.getMatricula());

            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("actualizar vehiculo", e);
            return 0;
        }
    }

    public int eliminar(String matricula) {

        String sql = "DELETE FROM vehiculos WHERE matricula = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, matricula);
            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("eliminar vehiculo", e);
            return 0;
        }
    }

    public List<Vehiculo> buscarPorCliente(Integer idCliente) {

        String sql = "SELECT * FROM vehiculos WHERE id_cliente = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorCliente vehiculo", e);
            return List.of();
        }
    }

    public List<Vehiculo> listarPorMarca(String marca) {

        String sql = "SELECT * FROM vehiculos WHERE marca = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, marca);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listarPorMarca vehiculo", e);
            return List.of();
        }
    }

}