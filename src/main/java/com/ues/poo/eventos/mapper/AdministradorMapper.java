package com.ues.poo.eventos.mapper;

import com.ues.poo.eventos.dto.AdministradorRequestDTO;
import com.ues.poo.eventos.dto.AdministradorResponseDTO;
import com.ues.poo.eventos.model.Administrador;

/** Conversion manual entre Administrador y sus DTO. */
public final class AdministradorMapper {

    private AdministradorMapper() {
    }

    public static Administrador aEntidad(AdministradorRequestDTO dto) {
        Administrador administrador = new Administrador();
        copiarDatos(administrador, dto);
        return administrador;
    }

    public static void copiarDatos(Administrador destino, AdministradorRequestDTO dto) {
        destino.setNombre(dto.getNombre().trim());
        destino.setApellido(dto.getApellido().trim());
        destino.setEmail(dto.getEmail().trim().toLowerCase());
        destino.setTelefono(dto.getTelefono());
        destino.setDepartamento(dto.getDepartamento());
    }

    public static AdministradorResponseDTO aRespuesta(Administrador administrador) {
        return AdministradorResponseDTO.builder()
                .id(administrador.getId())
                .nombre(administrador.getNombre())
                .apellido(administrador.getApellido())
                .email(administrador.getEmail())
                .telefono(administrador.getTelefono())
                .departamento(administrador.getDepartamento())
                .usuarioId(administrador.getUsuario() != null ? administrador.getUsuario().getId() : null)
                .fechaCreacion(administrador.getFechaCreacion())
                .fechaActualizacion(administrador.getFechaActualizacion())
                .build();
    }
}
