package com.ues.poo.eventos.mapper;

import com.ues.poo.eventos.dto.UbicacionRequestDTO;
import com.ues.poo.eventos.dto.UbicacionResponseDTO;
import com.ues.poo.eventos.dto.UbicacionResumenDTO;
import com.ues.poo.eventos.model.Ubicacion;

/** Conversion manual entre la entidad Ubicacion y sus DTO. */
public final class UbicacionMapper {

    private UbicacionMapper() {
    }

    public static Ubicacion aEntidad(UbicacionRequestDTO dto) {
        return Ubicacion.builder()
                .nombre(dto.getNombre())
                .direccion(dto.getDireccion())
                .ciudad(dto.getCiudad())
                .capacidad(dto.getCapacidad())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .build();
    }

    public static void copiarDatos(Ubicacion destino, UbicacionRequestDTO dto) {
        destino.setNombre(dto.getNombre());
        destino.setDireccion(dto.getDireccion());
        destino.setCiudad(dto.getCiudad());
        destino.setCapacidad(dto.getCapacidad());
        destino.setLatitud(dto.getLatitud());
        destino.setLongitud(dto.getLongitud());
    }

    public static UbicacionResponseDTO aRespuesta(Ubicacion ubicacion) {
        return UbicacionResponseDTO.builder()
                .id(ubicacion.getId())
                .nombre(ubicacion.getNombre())
                .direccion(ubicacion.getDireccion())
                .ciudad(ubicacion.getCiudad())
                .capacidad(ubicacion.getCapacidad())
                .latitud(ubicacion.getLatitud())
                .longitud(ubicacion.getLongitud())
                .build();
    }

    public static UbicacionResumenDTO aResumen(Ubicacion ubicacion) {
        if (ubicacion == null) {
            return null;
        }
        return UbicacionResumenDTO.builder()
                .id(ubicacion.getId())
                .nombre(ubicacion.getNombre())
                .ciudad(ubicacion.getCiudad())
                .build();
    }
}
