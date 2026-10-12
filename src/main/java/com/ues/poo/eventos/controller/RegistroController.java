package com.ues.poo.eventos.controller;

import com.ues.poo.eventos.dto.AsistenteEventoDTO;
import com.ues.poo.eventos.dto.DisponibilidadResponseDTO;
import com.ues.poo.eventos.dto.RegistroRequestDTO;
import com.ues.poo.eventos.dto.RegistroResponseDTO;
import com.ues.poo.eventos.service.RegistroService;
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
public class RegistroController {

    private final RegistroService registroService;

    @PostMapping("/eventos/{idEvento}/registros")
    public ResponseEntity<RegistroResponseDTO> registrar(@PathVariable Long idEvento,
                                                         @Valid @RequestBody RegistroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroService.registrar(idEvento, dto));
    }

    @GetMapping("/eventos/{idEvento}/disponibilidad")
    public ResponseEntity<DisponibilidadResponseDTO> disponibilidad(@PathVariable Long idEvento) {
        return ResponseEntity.ok(registroService.disponibilidad(idEvento));
    }

    @GetMapping("/eventos/{idEvento}/asistentes")
    public ResponseEntity<List<AsistenteEventoDTO>> asistentes(@PathVariable Long idEvento) {
        return ResponseEntity.ok(registroService.asistentesDeEvento(idEvento));
    }

    @GetMapping("/registros/{idRegistro}")
    public ResponseEntity<RegistroResponseDTO> obtener(@PathVariable Long idRegistro) {
        return ResponseEntity.ok(registroService.obtener(idRegistro));
    }

    @PutMapping("/registros/{idRegistro}/cancelar")
    public ResponseEntity<RegistroResponseDTO> cancelar(@PathVariable Long idRegistro) {
        return ResponseEntity.ok(registroService.cancelar(idRegistro));
    }

    @DeleteMapping("/registros/{idRegistro}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idRegistro) {
        registroService.eliminar(idRegistro);
        return ResponseEntity.noContent().build();
    }
}
