package com.ues.poo.eventos.controller;

import com.ues.poo.eventos.dto.EventoRequestDTO;
import com.ues.poo.eventos.dto.EventoResponseDTO;
import com.ues.poo.eventos.model.EstadoEvento;
import com.ues.poo.eventos.service.EventoService;
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
public class EventoController {

    private final EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar(
            @RequestParam(required = false) EstadoEvento estado,
            @RequestParam(required = false) Long ubicacionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(eventoService.listar(estado, ubicacionId, desde, hasta));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<EventoResponseDTO>> disponibles() {
        return ResponseEntity.ok(eventoService.disponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<EventoResponseDTO> crear(@Valid @RequestBody EventoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody EventoRequestDTO dto) {
        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
