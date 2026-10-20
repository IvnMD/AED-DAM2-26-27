package com.docencia.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteSqliteRepository implements ClienteRepository {
    private final String url;

    public ClienteSqliteRepository(String databasePath) {
        this.url = "JDBC:sqlite:" + databasePath;
        crearTablaSiNoExiste();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS cliente (dni TEXT PRIMARY KEY, nombre TEXT NOT NULL, email TEXT NOT NULL, ciudad TEXT NOT NULL)";
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            throw new RuntimeException("No se pudo crear la tabla cliente", exception);
        }
    }

    @Override
    public Boolean save(Cliente cliente) {
        String sql = "INSERT INTO cliente (dni, nombre, email, ciudad) VALUES (?,?,?,?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getCiudad());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Cliente findByDni(String dni) {
        String sql = "SELECT * FROM cliente where dni = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCliente(rs);
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public List<Cliente> findAll() {

        String sql = "SELECT * FROM cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapCliente(rs));
                }
            }
            return clientes;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Boolean update(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, email = ?, ciudad = ? WHERE dni = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getCiudad());

            ps.setString(4, cliente.getDni());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @Override
    public Boolean deleteByDni(String dni) {
        String sql = "DELETE FROM cliente WHERE dni = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Cliente> findByCiudad(String ciudad) {
        String sql = "SELECT * FROM cliente WHERE ciudad = ?";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ciudad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapCliente(rs));
                }
                return clientes;
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Cliente mapCliente(ResultSet rs) throws Exception {
        return new Cliente(
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("ciudad"));

    }
}