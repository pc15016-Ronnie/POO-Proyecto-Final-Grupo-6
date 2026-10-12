package com.ues.poo.eventos.mapper;

import com.ues.poo.eventos.dto.AsistenteRequestDTO;
import com.ues.poo.eventos.dto.AsistenteResponseDTO;
import com.ues.poo.eventos.dto.AsistenteResumenDTO;
import com.ues.poo.eventos.model.Asistente;

/** Conversion manual entre la entidad Asistente y sus DTO. */
public final class AsistenteMapper {

    private AsistenteMapper() {
    }

    public static Asistente aEntidad(AsistenteRequestDTO dto) {
        Asistente asistente = new Asistente();
        copiarDatos(asistente, dto);
        return asistente;
    }

    public static void copiarDatos(Asistente destino, AsistenteRequestDTO dto) {
        destino.setNombre(dto.getNombre());
        destino.setApellido(dto.getApellido());
        destino.setEmail(dto.getEmail().trim().toLowerCase());
        destino.setTelefono(dto.getTelefono());
    }

    public static AsistenteResponseDTO aRespuesta(Asistente asistente) {
        return AsistenteResponseDTO.builder()
                .id(asistente.getId())
                .nombre(asistente.getNombre())
                .apellido(asistente.getApellido())
                .email(asistente.getEmail())
                .telefono(asistente.getTelefono())
                .usuarioId(asistente.getUsuario() != null ? asistente.getUsuario().getId() : null)
                .fechaCreacion(asistente.getFechaCreacion())
                .fechaActualizacion(asistente.getFechaActualizacion())
                .build();
    }

    public static AsistenteResumenDTO aResumen(Asistente asistente) {
        if (asistente == null) {
            return null;
        }
        return AsistenteResumenDTO.builder()
                .id(asistente.getId())
                .nombre(asistente.getNombre())
                .apellido(asistente.getApellido())
                .email(asistente.getEmail())
                .build();
    }
}
