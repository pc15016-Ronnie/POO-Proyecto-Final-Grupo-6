package com.ues.poo.eventos.mapper;

import com.ues.poo.eventos.dto.EventoRequestDTO;
import com.ues.poo.eventos.dto.EventoResponseDTO;
import com.ues.poo.eventos.dto.EventoResumenDTO;
import com.ues.poo.eventos.model.Evento;
import com.ues.poo.eventos.model.Ubicacion;

/** Conversion manual entre la entidad Evento y sus DTO. */
public final class EventoMapper {

    private EventoMapper() {
    }

    public static Evento aEntidad(EventoRequestDTO dto, Ubicacion ubicacion) {
        return Evento.builder()
                .ubicacion(ubicacion)
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .estado(dto.getEstado())
                .build();
    }

    public static void copiarDatos(Evento destino, EventoRequestDTO dto, Ubicacion ubicacion) {
        destino.setUbicacion(ubicacion);
        destino.setNombre(dto.getNombre());
        destino.setDescripcion(dto.getDescripcion());
        destino.setFechaInicio(dto.getFechaInicio());
        destino.setFechaFin(dto.getFechaFin());
        destino.setCapacidadMaxima(dto.getCapacidadMaxima());
        destino.setEstado(dto.getEstado());
    }

    public static EventoResponseDTO aRespuesta(Evento evento, long inscritos) {
        int capacidad = evento.getCapacidadMaxima() == null ? 0 : evento.getCapacidadMaxima();
        long disponibles = Math.max(capacidad - inscritos, 0);
        return EventoResponseDTO.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .descripcion(evento.getDescripcion())
                .fechaInicio(evento.getFechaInicio())
                .fechaFin(evento.getFechaFin())
                .capacidadMaxima(evento.getCapacidadMaxima())
                .estado(evento.getEstado())
                .inscritos((int) inscritos)
                .cuposDisponibles((int) disponibles)
                .ubicacion(UbicacionMapper.aResumen(evento.getUbicacion()))
                .fechaCreacion(evento.getFechaCreacion())
                .fechaActualizacion(evento.getFechaActualizacion())
                .build();
    }

    public static EventoResumenDTO aResumen(Evento evento) {
        if (evento == null) {
            return null;
        }
        return EventoResumenDTO.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .fechaInicio(evento.getFechaInicio())
                .estado(evento.getEstado())
                .build();
    }
}
