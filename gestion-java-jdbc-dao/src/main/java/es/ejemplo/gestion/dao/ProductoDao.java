package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductoDao extends AbstractDao {
//    private  ConexionBD conexionBD;

    public ProductoDao(String url) {
        super(url);
    }


    @Override
    Producto mapToClass(ResultSet rs) {
        try {
            return new Producto(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getDouble(3));
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Producto> listar() {

        String query = "SELECT id, nombre, precio FROM Productos";

        return this.consultar(query);
    }

    public Optional<Producto> buscarPorId(int id) {
        String query = "SELECT id, nombre, precio FROM Productos WHERE id = ?";
        List<Producto> productos = null;
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, id);
            productos = this.consultar(ps);

            }
         catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        } if (productos != null && productos.size()>0)

    {
        return productos.stream().findFirst();
    }return Optional.empty();
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Productos (id, nombre, precio) VALUES (?, ?, ?)";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Productos SET nombre=?, precio=? WHERE id=?";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Productos WHERE id=?";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Producto> buscarPorNombre(String texto) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio FROM Productos WHERE nombre LIKE ? ORDER BY id DESC";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return productos;
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio FROM Productos "
                + "WHERE precio >= ? AND precio <= ? ORDER BY id";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDouble(1, minimo);
            ps.setDouble(2, maximo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return productos;
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Productos";
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

    public double precioMedio() {
        String sql = "SELECT AVG(precio) FROM Productos";
        try (Connection cn = this.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }


//    private Producto mapProducto(ResultSet rs) {
//        try {
//            return new Producto(
//                    rs.getInt(1),
//                    rs.getString(2),
//                    rs.getDouble(3));
//        } catch (SQLException e) {
//            throw new AplicationException(e.getMessage(), e);
//        }
//    }
}
