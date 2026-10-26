package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDao extends AbstractDao<Cliente> {

    public ClienteDao(String url) {
        super(url);
    }

    @Override
    protected Optional<Cliente> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("telefono")
            ));
        } catch (SQLException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }

    public List<Cliente> listar() {
        String sql = "SELECT * FROM clientes";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar clientes", e);
            return List.of();
        }
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscarPorId cliente", e);
            return Optional.empty();
        }
    }

    public int insertar(Cliente dato) {

        String sql = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException | RuntimeException e) {
            registrar("insertar cliente", e);
            return 0;
        }
    }

    public int actualizar(Cliente dato) {

        String sql = "UPDATE PDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id_cliente = ? ";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            ps.setInt(4, dato.getIdCliente());

            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("actualizar cliente", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {

        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection cn = obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException | RuntimeException e) {
            registrar("eliminar cliente", e);
            return 0;
        }
    }

}

