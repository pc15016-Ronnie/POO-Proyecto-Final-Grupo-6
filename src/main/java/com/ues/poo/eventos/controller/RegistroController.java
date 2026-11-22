package com.ues.poo.eventos.controller;

import com.ues.poo.eventos.dto.AsistenteEventoDTO;
import com.ues.poo.eventos.dto.DisponibilidadResponseDTO;
import com.ues.poo.eventos.dto.ErrorResponse;
import com.ues.poo.eventos.dto.RegistroRequestDTO;
import com.ues.poo.eventos.dto.RegistroResponseDTO;
import com.ues.poo.eventos.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** CU-14 a CU-17: inscripciones, aforo, cancelacion y listados de asistentes. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Registros", description = "Inscripciones, aforo, cancelacion y asistentes por evento")
@ApiResponse(responseCode = "400", description = "Datos invalidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "404", description = "Evento, asistente o registro no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "409", description = "Cupo, estado o registro duplicado incompatible",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "500", description = "Error inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class RegistroController {

    private final RegistroService registroService;

    @PostMapping("/eventos/{idEvento}/registros")
    @Operation(summary = "Registrar un asistente en un evento")
    public ResponseEntity<RegistroResponseDTO> registrar(@PathVariable Long idEvento,
                                                         @Valid @RequestBody RegistroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroService.registrar(idEvento, dto));
    }

    @GetMapping("/eventos/{idEvento}/disponibilidad")
    @Operation(summary = "Consultar la disponibilidad de cupo")
    public ResponseEntity<DisponibilidadResponseDTO> disponibilidad(@PathVariable Long idEvento) {
        return ResponseEntity.ok(registroService.disponibilidad(idEvento));
    }

    @GetMapping("/eventos/{idEvento}/asistentes")
    @Operation(summary = "Listar los asistentes activos de un evento")
    public ResponseEntity<List<AsistenteEventoDTO>> asistentes(@PathVariable Long idEvento) {
        return ResponseEntity.ok(registroService.asistentesDeEvento(idEvento));
    }

    @GetMapping("/registros/{idRegistro}")
    @Operation(summary = "Consultar un registro")
    public ResponseEntity<RegistroResponseDTO> obtener(@PathVariable Long idRegistro) {
        return ResponseEntity.ok(registroService.obtener(idRegistro));
    }

    @PutMapping("/registros/{idRegistro}/cancelar")
    @Operation(summary = "Cancelar un registro activo")
    public ResponseEntity<RegistroResponseDTO> cancelar(@PathVariable Long idRegistro) {
        return ResponseEntity.ok(registroService.cancelar(idRegistro));
    }

    @DeleteMapping("/registros/{idRegistro}")
    @Operation(summary = "Eliminar un registro cancelado")
    public ResponseEntity<Void> eliminar(@PathVariable Long idRegistro) {
        registroService.eliminar(idRegistro);
        return ResponseEntity.noContent().build();
    }
}
