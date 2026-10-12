package com.ues.poo.eventos.dto;

import com.ues.poo.eventos.model.EstadoEvento;
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
public class EventoResumenDTO {

    private Long id;
    private String nombre;
    private LocalDateTime fechaInicio;
    private EstadoEvento estado;
}
