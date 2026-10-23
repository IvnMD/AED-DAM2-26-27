package es.ejemplo.taller.dao;
import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import es.ejemplo.taller.modelo.Mecanico;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class MecanicoDao  extends ConexionBD {

    public MecanicoDao(String url) {
        super(url);
    }

    protected Optional<Mecanico> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Mecanico(
                    rs.getInt("id_mecanico"),
                    rs.getString("nombre"),
                    rs.getString("especialidad"),
                    rs.getBigDecimal("tarifa_hora")
            ));
        } catch (SQLException | RuntimeException e) {
            return Optional.empty();
        }
    }

    public List<Mecanico> listar() {
        List<Mecanico> lista = new ArrayList<>();
        String query = "SELECT id_mecanico, nombre, especialidad, tarifa_hora FROM Mecanicos";
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

    public Optional<Mecanico> buscarPorId(Integer id) {
        String query = "SELECT nombre, especialidad, tarifa_hora FROM Mecanicos WHERE id_mecanico = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setInt(1, id);
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

    public int insertar(Mecanico dato) {
        String query = "INSERT INTO Clientes (nombre, email, telefono) VALUES (?, ?, ?)";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int actualizar(Mecanico dato) {
        String query = "UPDATE mecanicos SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id_mecanico = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            ps.setInt(4, dato.getIdMecanico());

            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int eliminar(Integer id) {
        String query = "DELETE FROM mecanicos WHERE id_mecanico = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                return 1;
            }
        } catch (Exception e) {

        }
        return 0;
    }

    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        List<Mecanico> lista = new ArrayList<>();
        String query = "SELECT id_mecanico, especialidad, tarifa_hora FROM Mecanicos WHERE LOWER(nombre) LIKE ?";
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
