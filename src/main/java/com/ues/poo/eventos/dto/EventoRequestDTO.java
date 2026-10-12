package com.ues.poo.eventos.dto;

import com.ues.poo.eventos.model.EstadoEvento;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Datos de entrada para crear o actualizar un evento (CU-01 y CU-03).
 * La regla "la fecha debe ser futura" (RN-10) se valida en el servicio al crear,
 * para que actualizar un evento ya realizado siga siendo posible.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoRequestDTO {

    @NotNull(message = "La ubicacion es obligatoria")
    private Long ubicacionId;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @NotNull(message = "La capacidad maxima es obligatoria")
    @Min(value = 1, message = "La capacidad maxima debe ser mayor que cero")
    @Max(value = 1_000_000, message = "La capacidad maxima es demasiado grande")
    private Integer capacidadMaxima;

    @NotNull(message = "El estado del evento es obligatorio")
    private EstadoEvento estado;
}
