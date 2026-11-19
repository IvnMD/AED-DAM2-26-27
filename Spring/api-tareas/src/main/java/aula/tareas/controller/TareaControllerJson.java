package aula.tareas.controller;

import aula.tareas.api.TareaResquest;
import aula.tareas.service.ITareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/json/tareas")
@Tag(name="Tareas", description = "Operaciones crud de tareas en json")
public class TareaControllerJson {

    private final ITareaService serviceJson;

    public TareaControllerJson(@Qualifier("tareaServiceJson") ITareaService serviceJson) {
        this.serviceJson = serviceJson;
    }

    @GetMapping
    @Operation(summary = "Obtener todos las tareas desde el fichero json")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
    List<TareaResquest> listar() {
        return serviceJson.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la tarea a traves de su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "TareaResquest encontrada"),
            @ApiResponse(responseCode = "404", description = "TareaResquest no encontrada")
    })
    ResponseEntity<TareaResquest> buscar(@PathVariable long id) {
//return ResponseEntity.ofNullable(servicio.buscar(id));
        return null;
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "TareaResquest creada"),
            @ApiResponse(responseCode = "400", description = "TareaResquest existe")
    })
    ResponseEntity<TareaResquest> crear(@Valid @RequestBody TareaControllerJson.NuevaTarea peticion) {
        TareaResquest tareaResquest = serviceJson.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/json/tareas/" + tareaResquest.id())).body(tareaResquest);
    }

    public record NuevaTarea(@NotBlank String titulo) {}

}