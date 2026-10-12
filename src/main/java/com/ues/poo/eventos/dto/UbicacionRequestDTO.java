package com.ues.poo.eventos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos de entrada para crear o actualizar una ubicacion (CU-05 y CU-07). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionRequestDTO {

    @NotBlank(message = "El nombre de la ubicacion es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 200, message = "La direccion no puede superar 200 caracteres")
    private String direccion;

    @Size(max = 100, message = "La ciudad no puede superar 100 caracteres")
    private String ciudad;

    @NotNull(message = "La capacidad del lugar es obligatoria")
    @Min(value = 1, message = "La capacidad del lugar debe ser mayor que cero")
    @Max(value = 1_000_000, message = "La capacidad del lugar es demasiado grande")
    private Integer capacidad;

    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitud;

    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitud;
}
