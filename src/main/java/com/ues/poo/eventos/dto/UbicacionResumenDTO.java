package com.ues.poo.eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos de la ubicacion que viajan dentro de la respuesta de un evento. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionResumenDTO {

    private Long id;
    private String nombre;
    private String ciudad;
}
