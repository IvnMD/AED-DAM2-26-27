package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class VeterinarioDao implements IVeterinarioDao {

    private final String url;

    public VeterinarioDao(String url) {
        this.url = url;
    }

<<<<<<< Updated upstream
    private Veterinario mapVeterinario(ResultSet rs) throws SQLException {
        return new Veterinario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("especialidad"),
                rs.getBigDecimal("tarifa_hora")
        );
=======
    private Veterinario mapear(ResultSet rs) throws SQLException {
        Veterinario veterinario = new Veterinario();
        veterinario.setId(rs.getInt("id"));
        veterinario.setNombre(rs.getString("nombre"));
        veterinario.setEspecialidad(rs.getString("especialidad"));
        veterinario.setTarifaHora(rs.getBigDecimal("tarifa_hora"));
        return veterinario;
>>>>>>> Stashed changes
    }

    @Override
    public List<Veterinario> listar() {
        String sql = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios ORDER BY id";
<<<<<<< Updated upstream
        List<Veterinario> veterinarios = new ArrayList<>();
=======
        List<Veterinario> lista = new ArrayList<>();
>>>>>>> Stashed changes

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
<<<<<<< Updated upstream
                veterinarios.add(mapVeterinario(rs));
            }

            return veterinarios;
=======
                lista.add(mapear(rs));
            }
            return lista;
>>>>>>> Stashed changes

        } catch (SQLException e) {
            throw new DaoException("Error al listar veterinarios", e);
        }
    }

    @Override
    public Optional<Veterinario> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
<<<<<<< Updated upstream
                if (rs.next()) {
                    return Optional.of(mapVeterinario(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error al buscar veterinario por id", e);
=======
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new DaoException("Error al buscar el veterinario " + id, e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public int insertar(Veterinario entidad) {
        String sql = "INSERT INTO veterinarios (nombre, especialidad, tarifa_hora) VALUES (?, ?, ?)";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecialidad());
            ps.setBigDecimal(3, entidad.getTarifaHora());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entidad.setId(id);
                    return id;
                }
<<<<<<< Updated upstream
            }

            throw new DaoException("No se pudo obtener el identificador del veterinario insertado", null);

        } catch (SQLException e) {
            throw new DaoException("Error al insertar veterinario", e);
=======
                throw new DaoException("No se pudo obtener el identificador del veterinario insertado", null);
            }

        } catch (SQLException e) {
            throw new DaoException("Error al insertar el veterinario", e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public int actualizar(Veterinario entidad) {
        if (entidad == null || entidad.getId() == null) {
            throw new IllegalArgumentException("El veterinario debe tener un identificador");
        }

        String sql = "UPDATE veterinarios SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getEspecialidad());
            ps.setBigDecimal(3, entidad.getTarifaHora());
            ps.setInt(4, entidad.getId());

            return ps.executeUpdate();

        } catch (SQLException e) {
<<<<<<< Updated upstream
            throw new DaoException("Error al actualizar veterinario", e);
=======
            throw new DaoException("Error al actualizar el veterinario " + entidad.getId(), e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }

        String sql = "DELETE FROM veterinarios WHERE id = ?";

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
<<<<<<< Updated upstream
            throw new DaoException("Error al eliminar veterinario", e);
=======
            throw new DaoException("Error al eliminar el veterinario " + id, e);
>>>>>>> Stashed changes
        }
    }

    @Override
    public List<Veterinario> buscarPorEspecialidad(String especialidad) {
        String sql = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios WHERE especialidad = ? ORDER BY tarifa_hora, id";
<<<<<<< Updated upstream
        List<Veterinario> veterinarios = new ArrayList<>();
=======
        List<Veterinario> lista = new ArrayList<>();
>>>>>>> Stashed changes

        try (Connection cn = ConexionBD.abrir(url);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, especialidad);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
<<<<<<< Updated upstream
                    veterinarios.add(mapVeterinario(rs));
                }
            }

            return veterinarios;
=======
                    lista.add(mapear(rs));
                }
            }
            return lista;
>>>>>>> Stashed changes

        } catch (SQLException e) {
            throw new DaoException("Error al buscar veterinarios por especialidad", e);
        }
    }
}