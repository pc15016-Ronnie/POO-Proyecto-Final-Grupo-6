package com.ues.poo.eventos.controller;

import com.ues.poo.eventos.dto.UbicacionRequestDTO;
import com.ues.poo.eventos.dto.UbicacionResponseDTO;
import com.ues.poo.eventos.dto.ErrorResponse;
import com.ues.poo.eventos.service.UbicacionService;
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

/** CU-05 a CU-08: CRUD de ubicaciones. */
@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
@Tag(name = "Ubicaciones", description = "CRUD de los lugares que albergan eventos")
@ApiResponse(responseCode = "400", description = "Datos o parametros invalidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "404", description = "Ubicacion no encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "409", description = "La ubicacion tiene eventos asociados",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "500", description = "Error inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    @Operation(summary = "Listar ubicaciones")
    public ResponseEntity<List<UbicacionResponseDTO>> listar() {
        return ResponseEntity.ok(ubicacionService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una ubicacion")
    public ResponseEntity<UbicacionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear una ubicacion")
    public ResponseEntity<UbicacionResponseDTO> crear(@Valid @RequestBody UbicacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una ubicacion")
    public ResponseEntity<UbicacionResponseDTO> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody UbicacionRequestDTO dto) {
        return ResponseEntity.ok(ubicacionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una ubicacion sin eventos")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ubicacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
