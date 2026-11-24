package com.ues.poo.eventos.controller;

import com.ues.poo.eventos.dto.AdministradorRequestDTO;
import com.ues.poo.eventos.dto.AdministradorResponseDTO;
import com.ues.poo.eventos.dto.ErrorResponse;
import com.ues.poo.eventos.service.AdministradorService;
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

@RestController
@RequestMapping("/api/administradores")
@RequiredArgsConstructor
@Tag(name = "Administradores", description = "CRUD de perfiles administradores")
@ApiResponse(responseCode = "400", description = "Datos invalidos o rol de usuario incompatible",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "404", description = "Administrador o usuario no encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "409", description = "Correo o usuario ya vinculado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "500", description = "Error inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class AdministradorController {

    private final AdministradorService administradorService;

    @GetMapping
    @Operation(summary = "Listar administradores")
    public ResponseEntity<List<AdministradorResponseDTO>> listar() {
        return ResponseEntity.ok(administradorService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un administrador")
    public ResponseEntity<AdministradorResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(administradorService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear un administrador")
    public ResponseEntity<AdministradorResponseDTO> crear(@Valid @RequestBody AdministradorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administradorService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un administrador")
    public ResponseEntity<AdministradorResponseDTO> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody AdministradorRequestDTO dto) {
        return ResponseEntity.ok(administradorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un administrador")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        administradorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
