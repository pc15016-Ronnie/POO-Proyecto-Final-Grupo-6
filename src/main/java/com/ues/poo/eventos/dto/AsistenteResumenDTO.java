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
public class AsistenteResumenDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
}
