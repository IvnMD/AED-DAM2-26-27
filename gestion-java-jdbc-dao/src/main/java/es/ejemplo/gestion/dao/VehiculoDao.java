package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import es.ejemplo.gestion.model.Vehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehiculoDao extends AbstractDao<Vehiculo> {

    public VehiculoDao(String url) {
        super(url);
    }

    /**
     * @param rs ResultSet de una busqueda SQL
     * @return Objeto de la clase vehiculo mapeado
     */
    @Override
    Vehiculo mapToClass(ResultSet rs) {
        try{
            return new Vehiculo(
                    rs.getString("matricula"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("anio"));
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }


    public List<Vehiculo> listar() {
        String query = "SELECT matricula, marca, modelo, anio FROM Vehiculos ORDER BY matricula";
        return consultar(query);
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE matricula = ?";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?, ?, ?, ?)";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getMatricula());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean actualizar(Vehiculo vehiculo) {
        String sql = "UPDATE Vehiculos SET marca=?, modelo=?, anio=? WHERE matricula=?";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getMarca());
            ps.setString(2, vehiculo.getModelo());
            ps.setInt(3, vehiculo.getAnio());
            ps.setString(4, vehiculo.getMatricula());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean eliminar(String matricula) {
        String sql = "DELETE FROM Vehiculos WHERE matricula=?";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos "
                + "WHERE marca LIKE ? ORDER BY matricula";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return vehiculos;
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos "
                + "WHERE anio BETWEEN ? AND ? ORDER BY anio, matricula";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, desde);
            ps.setInt(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return vehiculos;
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Vehiculos";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public Map<String, Integer> contarPorMarca() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT marca, COUNT(*) FROM Vehiculos GROUP BY marca ORDER BY marca";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return resultado;
    }

}