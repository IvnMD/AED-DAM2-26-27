package aula.tareas.controller;

import java.net.URI;
import java.util.List;


import aula.tareas.api.TareaResquest;
import aula.tareas.service.TareaServiceCsv;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tareas")
class TareaControllerCsv {
    private final TareaServiceCsv servicio;

    TareaControllerCsv(@Qualifier("tareaServiceCsv") TareaServiceCsv servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    List<TareaResquest> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    ResponseEntity<TareaResquest> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(servicio.buscar(id));
    }

    @PostMapping
    ResponseEntity<TareaResquest> crear(@Valid @RequestBody NuevaTarea peticion) {
        TareaResquest tareaResquest = servicio.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/tareas/" + tareaResquest.id())).body(tareaResquest);
    }

    public record NuevaTarea(@NotBlank String titulo) {}
}