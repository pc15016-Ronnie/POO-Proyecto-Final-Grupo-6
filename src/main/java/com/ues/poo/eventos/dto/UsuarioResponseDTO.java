package com.ues.poo.eventos.dto;

import com.ues.poo.eventos.model.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Representacion publica de un usuario, sin datos de contrasena. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
