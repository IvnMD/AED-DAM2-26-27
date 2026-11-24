package ies.puerto.peliculas.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ies.puerto.peliculas.controller.mapper.PeliculaApiMapper;
import ies.puerto.peliculas.model.Pelicula;
import ies.puerto.peliculas.service.PeliculaService;
import ies.puerto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puerto.peliculas.service.modelo.DatosEdicionPelicula;

@WebMvcTest(PeliculaJdbcController.class)
@ActiveProfiles("jdbc")
@Import(PeliculaApiMapper.class)
class PeliculaJdbcControllerTest {

    private static final String RUTA = "/api/v1/jdbc/peliculas";

    private static final String CREACION_VALIDA = """
            {
                "codigo": "PEL003",
                "titulo": "El castillo ambulante",
                "anioEstreno": 2004,
                "duracionMinutos": 119
            }
            """;

    private static final String EDICION_VALIDA = """
            {
                "titulo": "El castillo ambulante - actualizada",
                "anioEstreno": 2004,
                "duracionMinutos": 120
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PeliculaService service;

    @Test
    void listarDevuelve200ConPeliculas() throws Exception {
        when(service.listar()).thenReturn(List.of(
                new Pelicula("PEL001", "El viaje de Chihiro", 2001, 125)
        ));

        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("PEL001"))
                .andExpect(jsonPath("$[0].titulo").value("El viaje de Chihiro"))
                .andExpect(jsonPath("$[0].anioEstreno").value(2001))
                .andExpect(jsonPath("$[0].duracionMinutos").value(125));
    }

    @Test
    void listarSinPeliculasDevuelve200ConListaVacia() throws Exception {
        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void obtenerExistenteDevuelve200() throws Exception {
        when(service.obtener("PEL001")).thenReturn(Optional.of(
                new Pelicula("PEL001", "El viaje de Chihiro", 2001, 125)
        ));

        mockMvc.perform(get(RUTA + "/PEL001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("PEL001"))
                .andExpect(jsonPath("$.titulo").value("El viaje de Chihiro"))
                .andExpect(jsonPath("$.anioEstreno").value(2001))
                .andExpect(jsonPath("$.duracionMinutos").value(125));
    }

    @Test
    void obtenerInexistenteDevuelve404() throws Exception {
        when(service.obtener("PEL999")).thenReturn(Optional.empty());

        mockMvc.perform(get(RUTA + "/PEL999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail", containsString("PEL999")));
    }

    @Test
    void obtenerCodigoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(get(RUTA + "/ABC"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void crearDevuelve201ConLocation() throws Exception {
        when(service.crear(any(DatosCreacionPelicula.class)))
                .thenReturn(Optional.of(
                        new Pelicula(
                                "PEL003",
                                "El castillo ambulante",
                                2004,
                                119
                        )
                ));

        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREACION_VALIDA))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", RUTA + "/PEL003"))
                .andExpect(jsonPath("$.codigo").value("PEL003"))
                .andExpect(jsonPath("$.titulo").value("El castillo ambulante"))
                .andExpect(jsonPath("$.anioEstreno").value(2004))
                .andExpect(jsonPath("$.duracionMinutos").value(119));

        verify(service).crear(new DatosCreacionPelicula(
                "PEL003",
                "El castillo ambulante",
                2004,
                119
        ));
    }

    @Test
    void crearDuplicadaDevuelve409() throws Exception {
        when(service.crear(any(DatosCreacionPelicula.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREACION_VALIDA))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail", containsString("PEL003")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            """
            {"codigo":"ABC","titulo":"Prueba","anioEstreno":2004,"duracionMinutos":119}
            """,
            """
            {"codigo":"PEL003","titulo":"   ","anioEstreno":2004,"duracionMinutos":119}
            """,
            """
            {"codigo":"PEL003","titulo":"Prueba","anioEstreno":1887,"duracionMinutos":119}
            """,
            """
            {"codigo":"PEL003","titulo":"Prueba","anioEstreno":2101,"duracionMinutos":119}
            """,
            """
            {"codigo":"PEL003","titulo":"Prueba","anioEstreno":2004,"duracionMinutos":0}
            """,
            """
            {"codigo":"PEL003","titulo":"Prueba","anioEstreno":2004,"duracionMinutos":1001}
            """
    })
    void crearConDatosInvalidosDevuelve400(String cuerpo) throws Exception {
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void crearConTituloDemasiadoLargoDevuelve400() throws Exception {
        String cuerpo = """
                {
                    "codigo": "PEL003",
                    "titulo": "%s",
                    "anioEstreno": 2004,
                    "duracionMinutos": 119
                }
                """.formatted("A".repeat(121));

        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void actualizarExistenteDevuelve200YUsaCodigoDeLaRuta() throws Exception {
        when(service.actualizar(
                eq("PEL003"),
                any(DatosEdicionPelicula.class)
        )).thenReturn(Optional.of(
                new Pelicula(
                        "PEL003",
                        "El castillo ambulante - actualizada",
                        2004,
                        120
                )
        ));

        mockMvc.perform(put(RUTA + "/PEL003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EDICION_VALIDA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("PEL003"))
                .andExpect(jsonPath("$.titulo")
                        .value("El castillo ambulante - actualizada"))
                .andExpect(jsonPath("$.anioEstreno").value(2004))
                .andExpect(jsonPath("$.duracionMinutos").value(120));

        verify(service).actualizar(
                "PEL003",
                new DatosEdicionPelicula(
                        "El castillo ambulante - actualizada",
                        2004,
                        120
                )
        );
    }

    @Test
    void actualizarInexistenteDevuelve404() throws Exception {
        when(service.actualizar(
                eq("PEL999"),
                any(DatosEdicionPelicula.class)
        )).thenReturn(Optional.empty());

        mockMvc.perform(put(RUTA + "/PEL999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EDICION_VALIDA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail", containsString("PEL999")));
    }

    @Test
    void actualizarCodigoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(put(RUTA + "/ABC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EDICION_VALIDA))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            """
            {"titulo":"   ","anioEstreno":2004,"duracionMinutos":119}
            """,
            """
            {"titulo":"Prueba","anioEstreno":1887,"duracionMinutos":119}
            """,
            """
            {"titulo":"Prueba","anioEstreno":2101,"duracionMinutos":119}
            """,
            """
            {"titulo":"Prueba","anioEstreno":2004,"duracionMinutos":0}
            """,
            """
            {"titulo":"Prueba","anioEstreno":2004,"duracionMinutos":1001}
            """
    })
    void actualizarConDatosInvalidosDevuelve400(String cuerpo) throws Exception {
        mockMvc.perform(put(RUTA + "/PEL003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void eliminarCodigoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(delete(RUTA + "/ABC"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}