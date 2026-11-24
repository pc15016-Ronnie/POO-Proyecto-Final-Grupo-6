package com.ues.poo.eventos.controller;

import com.ues.poo.eventos.dto.AsistenteRequestDTO;
import com.ues.poo.eventos.dto.AsistenteResponseDTO;
import com.ues.poo.eventos.dto.ErrorResponse;
import com.ues.poo.eventos.service.AsistenteService;
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

/** CU-09 a CU-12: CRUD de asistentes. */
@RestController
@RequestMapping("/api/asistentes")
@RequiredArgsConstructor
@Tag(name = "Asistentes", description = "CRUD de las personas que se inscriben a eventos")
@ApiResponse(responseCode = "400", description = "Datos invalidos o rol de usuario incompatible",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "404", description = "Asistente o usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "409", description = "Correo duplicado, usuario vinculado o registros activos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "500", description = "Error inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class AsistenteController {

    private final AsistenteService asistenteService;

    @GetMapping
    @Operation(summary = "Listar asistentes")
    public ResponseEntity<List<AsistenteResponseDTO>> listar() {
        return ResponseEntity.ok(asistenteService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un asistente")
    public ResponseEntity<AsistenteResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(asistenteService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear un asistente")
    public ResponseEntity<AsistenteResponseDTO> crear(@Valid @RequestBody AsistenteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenteService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un asistente")
    public ResponseEntity<AsistenteResponseDTO> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody AsistenteRequestDTO dto) {
        return ResponseEntity.ok(asistenteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un asistente sin registros activos")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
