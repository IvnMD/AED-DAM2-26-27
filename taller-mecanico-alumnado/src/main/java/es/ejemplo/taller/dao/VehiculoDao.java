package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Mecanico;
import es.ejemplo.taller.modelo.Vehiculo;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class VehiculoDao  extends ConexionBD {

    public VehiculoDao(String url) { super(url); }


    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Vehiculo(
                    rs.getString("matricula"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("anio"),
                    rs.getInt("id_cliente")
            ));
        } catch (SQLException | RuntimeException e) {
            return Optional.empty();
        }
    }

    public List<Vehiculo> listar() {
        List<Vehiculo> lista = new ArrayList<>();
        String query = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRsToClass(rs));
            }
            return lista;
        } catch (Exception e) {
            return List.of();
        }
    }
    public Optional<Vehiculo> buscarPorId(String id) {
        String query = "SELECT marca, modelo , anio, id_cliente FROM vehiculos WHERE matricula = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRsToClass(rs);
                }
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }
    public int insertar(Vehiculo dato) {
        String query = "INSERT INTO vehiculos (matricula, marca, modelo, anio, id_cliente) values (?, ?, ?, ?, ?)";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, dato.getMatricula());
            ps.setString(2, dato.getMarca());
            ps.setString(3, dato.getModelo());
            ps.setInt(4, dato.getAnio());
            ps.setInt(5, dato.getIdCliente());

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public int actualizar(Vehiculo dato) {
        String query = "UPDATE vehiculos SET matricula = ?, marca = ?, modelo = ?, anio = ?, id_cliente = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, dato.getMatricula());
            ps.setString(2, dato.getMarca());
            ps.setString(3, dato.getModelo());
            ps.setInt(4, dato.getAnio());
            ps.setInt(5, dato.getIdCliente());

            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public int eliminar(String id) {
        String  query = "DELETE FROM vehiculos WHERE matricula = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, id);
            if (ps.executeUpdate() > 0) {
                return 1;
            }
        } catch (Exception e) {

        }
        return 0;
    }
    public List<Vehiculo> buscarPorCliente(Integer idCliente) {
        List<Vehiculo> lista = new ArrayList<>();
        String query = "SELECT matricula, marca, modelo, anio FROM vehiculos WHERE id_cliente = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mapRsToClass(rs);
            }
            return lista;
        } catch (Exception e) {
            return List.of();
        }
    }
    public List<Vehiculo> listarPorMarca(String marca) {
        List<Vehiculo> lista = new ArrayList<>();
        String query = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos  group by marca";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mapRsToClass(rs);
            }
            return lista;
        } catch (Exception e) {
            return List.of();
        }
    }
}
