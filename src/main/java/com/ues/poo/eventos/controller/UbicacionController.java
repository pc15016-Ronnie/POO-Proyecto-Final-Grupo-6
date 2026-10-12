package com.ues.poo.eventos.controller;

import com.ues.poo.eventos.dto.UbicacionRequestDTO;
import com.ues.poo.eventos.dto.UbicacionResponseDTO;
import com.ues.poo.eventos.service.UbicacionService;
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
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    public ResponseEntity<List<UbicacionResponseDTO>> listar() {
        return ResponseEntity.ok(ubicacionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbicacionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<UbicacionResponseDTO> crear(@Valid @RequestBody UbicacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbicacionResponseDTO> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody UbicacionRequestDTO dto) {
        return ResponseEntity.ok(ubicacionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ubicacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
