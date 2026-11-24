package ies.puerto.peliculas.repository.jdbc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class PeliculaRowMapperTest {

    private final PeliculaRowMapper mapper = new PeliculaRowMapper();

    @Test
    void mapRowLeeLasColumnasYConservaTodosLosDatos() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getString("pelicula_codigo")).thenReturn("PEL001");
        when(resultSet.getString("titulo_texto"))
                .thenReturn("El viaje de Chihiro");
        when(resultSet.getInt("estreno_anio")).thenReturn(2001);
        when(resultSet.getInt("duracion_minutos")).thenReturn(125);

        PeliculaEntity fila = mapper.mapRow(resultSet, 0);

        assertAll(
                () -> assertEquals("PEL001", fila.getCodigoFila()),
                () -> assertEquals("El viaje de Chihiro", fila.getTituloFila()),
                () -> assertEquals(2001, fila.getAnioFila()),
                () -> assertEquals(125, fila.getMinutosFila())
        );
    }
}