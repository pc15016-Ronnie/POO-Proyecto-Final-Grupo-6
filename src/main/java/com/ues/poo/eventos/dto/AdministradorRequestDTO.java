package com.ues.poo.eventos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos de entrada para crear o actualizar un administrador. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 80, message = "El apellido no puede superar 80 caracteres")
    private String apellido;

    @NotBlank(message = "El correo electronico es obligatorio")
    @Email(message = "El correo electronico no tiene un formato valido")
    @Size(max = 120, message = "El correo no puede superar 120 caracteres")
    private String email;

    @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
    private String telefono;

    @Size(max = 80, message = "El departamento no puede superar 80 caracteres")
    private String departamento;

    private Long usuarioId;
}
