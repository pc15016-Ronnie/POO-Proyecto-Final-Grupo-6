package com.ues.poo.eventos.mapper;

import com.ues.poo.eventos.dto.RegistroResponseDTO;
import com.ues.poo.eventos.model.Registro;

/** Conversion manual entre la entidad Registro y su DTO de salida. */
public final class RegistroMapper {

    private RegistroMapper() {
    }

    public static RegistroResponseDTO aRespuesta(Registro registro, long cuposDisponibles) {
        return RegistroResponseDTO.builder()
                .id(registro.getId())
                .estado(registro.getEstado())
                .fechaRegistro(registro.getFechaRegistro())
                .fechaCancelacion(registro.getFechaCancelacion())
                .evento(EventoMapper.aResumen(registro.getEvento()))
                .asistente(AsistenteMapper.aResumen(registro.getAsistente()))
                .cuposDisponibles((int) Math.max(cuposDisponibles, 0))
                .build();
    }
}
