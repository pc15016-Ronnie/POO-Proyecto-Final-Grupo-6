package com.ues.poo.eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Respuesta de CU-16: capacidad maxima, inscritos y cupos disponibles. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadResponseDTO {

    private Long eventoId;
    private String evento;
    private Integer capacidadMaxima;
    private Long inscritos;
    private Long disponibles;
}
