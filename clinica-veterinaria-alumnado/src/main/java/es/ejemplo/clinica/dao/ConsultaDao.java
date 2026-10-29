package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Consulta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ConsultaDao implements IConsultaDao {

    private final String url;

    public ConsultaDao(String url) {
        this.url = url;
    }

    private Consulta mapConsulta(ResultSet rs) throws SQLException {
        return new Consulta(
                rs.getInt("id"),
                rs.getInt("id_mascota"),
                rs.getInt("id_veterinario"),
                rs.getString("fecha"),
                rs.getBigDecimal("horas"),
                rs.getString("estado")
        );
    }

    @Override
    public List<Consulta> listar() {
        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas ORDER BY id";
        List<Consulta> consultas = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                consultas.add(mapConsulta(rs));
            }

            return consultas;

        } catch (SQLException e) {
            throw new DaoException("Error al listar consultas", e);
        }
    }

    @Override
    public Optional<Consulta> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapConsulta(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar la consulta por id", e);
        }
    }

    @Override
    public int insertar(Consulta entidad) {
        String sql = "INSERT INTO consultas (id_mascota, id_veterinario, fecha, horas, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entidad.getIdMascota());
            ps.setInt(2, entidad.getIdVeterinario());
            ps.setString(3, entidad.getFecha());
            ps.setBigDecimal(4, entidad.getHoras());
            ps.setString(5, entidad.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entidad.setId(id);
                    return id;
                }
            }

            throw new DaoException("No se pudo obtener el identificador de la consulta insertada", null);

        } catch (SQLException e) {
            throw new DaoException("Error al insertar la consulta", e);
        }
    }

    @Override
    public int actualizar(Consulta entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException("La consulta debe tener un identificador");
        }

        String sql = "UPDATE consultas SET id_mascota = ?, id_veterinario = ?, fecha = ?, horas = ?, estado = ? WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, entidad.getIdMascota());
            ps.setInt(2, entidad.getIdVeterinario());
            ps.setString(3, entidad.getFecha());
            ps.setBigDecimal(4, entidad.getHoras());
            ps.setString(5, entidad.getEstado());
            ps.setInt(6, entidad.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al actualizar la consulta", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }

        String sql = "DELETE FROM consultas WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Error al eliminar la consulta", e);
        }
    }

    @Override
    public List<Consulta> buscarPorMascota(Integer idMascota) {
        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas WHERE id_mascota = ? ORDER BY id";
        List<Consulta> consultas = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMascota);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultas.add(mapConsulta(rs));
                }
            }

            return consultas;

        } catch (SQLException e) {
            throw new DaoException("Error al buscar consultas por mascota", e);
        }
    }

    @Override
    public List<Consulta> buscarPorVeterinario(Integer idVeterinario) {
        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas WHERE id_veterinario = ? ORDER BY id";
        List<Consulta> consultas = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idVeterinario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultas.add(mapConsulta(rs));
                }
            }

            return consultas;

        } catch (SQLException e) {
            throw new DaoException("Error al buscar consultas por veterinario", e);
        }
    }

    @Override
    public List<Consulta> listarProgramadas() {
        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado FROM consultas WHERE estado = 'PROGRAMADA' ORDER BY id";
        List<Consulta> consultas = new ArrayList<>();

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                consultas.add(mapConsulta(rs));
            }

            return consultas;

        } catch (SQLException e) {
            throw new DaoException("Error al listar consultas programadas", e);
        }
    }
}