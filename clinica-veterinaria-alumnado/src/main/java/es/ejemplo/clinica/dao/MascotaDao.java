package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Mascota;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MascotaDao implements IMascotaDao {

    private final String url;

    public MascotaDao(String url) {
        this.url = url;
    }

    private Mascota mapMascota(ResultSet rs) throws SQLException {
        return new Mascota(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("especie"),
                rs.getString("raza"),
                rs.getString("fecha_nacimiento"),
                rs.getInt("id_cliente")
        );
    }

    @Override
    public List<Mascota> listar() {
        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas ORDER BY id";
        List<Mascota> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapMascota(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al listar mascotas", e);
        }
    }

    @Override
    public Optional<Mascota> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapMascota(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar la mascota por id", e);
        }
    }

    @Override
    public int insertar(Mascota entidad) {
        String sql = "INSERT INTO mascotas (nombre, especie, raza, fecha_nacimiento, id_cliente) VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecie());
            ps.setString(3, entidad.getRaza());
            ps.setString(4, entidad.getFechaNacimiento());
            ps.setInt(5, entidad.getIdCliente());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entidad.setId(id);
                    return id;
                }
            }

            throw new DaoException("No se pudo obtener el identificador de la mascota insertada", null);

        } catch (SQLException e) {
            throw new DaoException("Error al insertar la mascota", e);
        }
    }

    @Override
    public int actualizar(Mascota entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException("La mascota debe tener un identificador");
        }

        String sql = "UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, fecha_nacimiento = ?, id_cliente = ? WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecie());
            ps.setString(3, entidad.getRaza());
            ps.setString(4, entidad.getFechaNacimiento());
            ps.setInt(5, entidad.getIdCliente());
            ps.setInt(6, entidad.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar la mascota", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }

        String sql = "DELETE FROM mascotas WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar la mascota", e);
        }
    }

    @Override
    public List<Mascota> buscarPorCliente(Integer idCliente) {
        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE id_cliente = ? ORDER BY id";
        List<Mascota> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapMascota(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al buscar mascotas por cliente", e);
        }
    }

    @Override
    public List<Mascota> listarPorEspecie(String especie) {
        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE especie = ? ORDER BY id";
        List<Mascota> lista = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, especie);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapMascota(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new DaoException("Error al listar mascotas por especie", e);
        }
    }
}