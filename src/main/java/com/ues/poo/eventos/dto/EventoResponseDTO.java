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
public class EventoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer capacidadMaxima;
    private EstadoEvento estado;
    private Integer inscritos;
    private Integer cuposDisponibles;
    private UbicacionResumenDTO ubicacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
