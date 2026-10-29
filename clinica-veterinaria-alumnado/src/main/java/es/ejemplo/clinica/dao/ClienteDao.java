package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClienteDao implements IClienteDao {

    private final String url;

    public ClienteDao(String url) {
        this.url = url;
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("telefono")
        );
    }

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT id, nombre, email, telefono FROM clientes ORDER BY id";
        List<Cliente> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapCliente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al listar clientes", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, email, telefono FROM clientes WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCliente(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar cliente por id", e);
        }
    }

    @Override
    public int insertar(Cliente entidad) {
        String sql = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEmail());
            ps.setString(3, entidad.getTelefono());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entidad.setId(id);
                    return id;
                }

                throw new DaoException("No se pudo obtener el identificador del cliente insertado", null);
            }

        } catch (SQLException e) {
            throw new DaoException("Error al insertar cliente", e);
        }
    }

    @Override
    public int actualizar(Cliente entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException("El cliente debe tener un identificador");
        }

        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEmail());
            ps.setString(3, entidad.getTelefono());
            ps.setInt(4, entidad.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar cliente", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }

        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar cliente", e);
        }
    }
}