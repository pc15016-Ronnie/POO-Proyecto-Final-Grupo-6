package com.ues.poo.eventos.controller;

import com.ues.poo.eventos.dto.EventoRequestDTO;
import com.ues.poo.eventos.dto.EventoResponseDTO;
import com.ues.poo.eventos.dto.ErrorResponse;
import com.ues.poo.eventos.model.EstadoEvento;
import com.ues.poo.eventos.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/** CU-01 a CU-04 y CU-13: CRUD de eventos y consulta de eventos disponibles. */
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "CRUD, filtros y consulta de eventos disponibles")
@ApiResponse(responseCode = "400", description = "Datos invalidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "404", description = "Evento o ubicacion no encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "409", description = "Regla de capacidad o asociacion incumplida",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "500", description = "Error inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class EventoController {

    private final EventoService eventoService;

    @GetMapping
    @Operation(summary = "Listar y filtrar eventos")
    public ResponseEntity<List<EventoResponseDTO>> listar(
            @Parameter(description = "Estado del evento") @RequestParam(required = false) EstadoEvento estado,
            @Parameter(description = "Identificador de la ubicacion") @RequestParam(required = false) Long ubicacionId,
            @Parameter(description = "Fecha ISO-8601 minima", example = "2026-11-23T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @Parameter(description = "Fecha ISO-8601 maxima", example = "2026-12-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(eventoService.listar(estado, ubicacionId, desde, hasta));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar eventos publicados, futuros y con cupo")
    public ResponseEntity<List<EventoResponseDTO>> disponibles() {
        return ResponseEntity.ok(eventoService.disponibles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un evento")
    public ResponseEntity<EventoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear un evento")
    public ResponseEntity<EventoResponseDTO> crear(@Valid @RequestBody EventoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un evento respetando los inscritos activos")
    public ResponseEntity<EventoResponseDTO> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody EventoRequestDTO dto) {
        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento sin inscritos activos")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
