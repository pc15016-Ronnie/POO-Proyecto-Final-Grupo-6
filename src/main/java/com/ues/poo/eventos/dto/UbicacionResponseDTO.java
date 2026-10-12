package com.ues.poo.eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionResponseDTO {

    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private Integer capacidad;
    private Double latitud;
    private Double longitud;
}
