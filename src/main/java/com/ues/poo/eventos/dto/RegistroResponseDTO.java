package com.ues.poo.eventos.dto;

import com.ues.poo.eventos.model.EstadoRegistro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroResponseDTO {

    private Long id;
    private EstadoRegistro estado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaCancelacion;
    private EventoResumenDTO evento;
    private AsistenteResumenDTO asistente;
    private Integer cuposDisponibles;
}
