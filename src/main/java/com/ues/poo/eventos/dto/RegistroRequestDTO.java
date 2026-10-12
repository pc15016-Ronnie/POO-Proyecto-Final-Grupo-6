package com.ues.poo.eventos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos de entrada para inscribir a un asistente en un evento (CU-14). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequestDTO {

    @NotNull(message = "El asistente es obligatorio")
    private Long asistenteId;
}
