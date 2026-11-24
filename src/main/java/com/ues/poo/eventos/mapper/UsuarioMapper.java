package com.ues.poo.eventos.mapper;

import com.ues.poo.eventos.dto.UsuarioResponseDTO;
import com.ues.poo.eventos.model.Usuario;

/** Conversion entre Usuario y su representacion publica. */
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponseDTO aRespuesta(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }
}
