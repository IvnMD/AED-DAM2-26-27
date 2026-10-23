package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDao extends ConexionBD {

    public ClienteDao(String url) {
        super(url);
    }

    protected Optional<Cliente> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("telefono")
            ));
        } catch (SQLException | RuntimeException e) {
            return Optional.empty();
        }
    }

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT id_cliente, nombre, email, telefono FROM Clientes";
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

    public Optional<Cliente> buscarPorId(Integer id) {
        String query = "SELECT id_cliente, nombre, email, telefono FROM Clientes WHERE id_cliente = ?";
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

    public int insertar(Cliente dato) {
        String query = "INSERT INTO Clientes (nombre, email, telefono) VALUES (?, ?, ?)";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());

            return ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public int actualizar(Cliente dato) {
        String query = "UPDATE Clientes SET nombre = ?, email = ?, telefono = ? WHERE id_cliente = ?";
        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            ps.setInt(4, dato.getIdCliente());

            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int eliminar(Integer id) {
        String query = "DELETE FROM Clientes WHERE id_cliente = ?";
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
}
