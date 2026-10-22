package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Vehiculo;
import java.sql.*;
import java.util.*;

public class VehiculoDao extends AbstractDao<Vehiculo> {
    public VehiculoDao(String url) { super(url); }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        try{
            return Optional.of(new Vehiculo(
                    rs.getString("matricula"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("anio")));
        }catch(Exception e){
            registrar("mapRsToClass producto", e);
            return Optional.empty();
        }
    }

    public List<Vehiculo> listar() {
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos ORDER BY matricula";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar vehiculoa", e);
            return List.of();
        }
    }

    public Optional<Vehiculo> buscarPorId(String matricula) {
//        if (matricula == null) {
//            return Optional.empty();
//        }
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE matricula = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorId vehiculos", e);
            return Optional.empty();
        }
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        return buscarPorId(matricula);
    }

    public boolean eliminar(String matricula) {
        String sql = "DELETE FROM Vehiculos WHERE matricula = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, matricula);
            return ejecutar(ps);
        } catch (RuntimeException | SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean insertar(Vehiculo v) {
        String sql = "INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?, ?, ?, ?)" ;
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, v.getMatricula());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            return ejecutar(ps);

        } catch (RuntimeException | SQLException e){
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean actualizar(Vehiculo v) {
        String sql = "UPDATE Vehiculos SET marca = ?,  modelo = ?, anio = ? WHERE matricula = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, v.getMarca());
            ps.setString(2, v.getModelo());
            ps.setInt(3, v.getAnio());
            ps.setString(4, v.getMatricula());
            return ejecutar(ps);

        } catch (RuntimeException | SQLException e){
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE LOWER(marca) = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, texto.toLowerCase());
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar productos", e);
            return List.of();
        }
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        // TODO: BETWEEN inclusivo
        String sql = "SELECT matricula, marca, modelo, anio From Vehiculos WHERE anio BETWEEN ? AND ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, desde);
            ps.setInt(2, hasta);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar productos", e);
            return List.of();
        }
    }

    public int contar() {
            String sql = "SELECT COUNT(*) FROM Vehiculos";
            try (Connection cn = obtenerConexion();
                 PreparedStatement ps = cn.prepareStatement(sql)){
                return consultarEntero(ps);
            } catch (Exception e) {
                throw new AplicationException(e.getMessage(), e);
            }
    }

    public Map<String, Integer> contarPorMarca() {
        String sql = "SELECT marca, COUNT(*) FROM Vehiculos GROUP BY marca ORDER BY marca";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            return consultarAgrupado(ps);
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

}
