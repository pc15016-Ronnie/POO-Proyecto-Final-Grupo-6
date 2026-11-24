package com.ues.poo.eventos.dto;

import com.ues.poo.eventos.model.RolUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos de entrada para crear o actualizar credenciales de usuario. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 60, message = "El nombre de usuario no puede superar 60 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$",
            message = "El nombre de usuario solo puede contener letras, numeros, punto, guion y guion bajo")
    private String username;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY,
            description = "Contrasena en texto plano; se almacena codificada con BCrypt y nunca se devuelve")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;

    private Boolean activo = Boolean.TRUE;
}
