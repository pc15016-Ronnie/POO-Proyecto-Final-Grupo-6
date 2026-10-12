package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.AsistenteEventoDTO;
import com.ues.poo.eventos.dto.DisponibilidadResponseDTO;
import com.ues.poo.eventos.dto.RegistroRequestDTO;
import com.ues.poo.eventos.dto.RegistroResponseDTO;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.mapper.AsistenteMapper;
import com.ues.poo.eventos.mapper.RegistroMapper;
import com.ues.poo.eventos.model.Asistente;
import com.ues.poo.eventos.model.EstadoEvento;
import com.ues.poo.eventos.model.EstadoRegistro;
import com.ues.poo.eventos.model.Evento;
import com.ues.poo.eventos.model.Registro;
import com.ues.poo.eventos.repository.AsistenteRepository;
import com.ues.poo.eventos.repository.EventoRepository;
import com.ues.poo.eventos.repository.RegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** CU-14 a CU-17: inscripciones, aforo, cancelacion y listados. */
@Service
@RequiredArgsConstructor
public class RegistroService {

    private final RegistroRepository registroRepository;
    private final EventoRepository eventoRepository;
    private final AsistenteRepository asistenteRepository;

    /**
     * CU-14. Se bloquea la fila del evento para que dos inscripciones
     * simultaneas no puedan sobrepasar la capacidad maxima.
     */
    @Transactional
    public RegistroResponseDTO registrar(Long eventoId, RegistroRequestDTO dto) {
        Evento evento = eventoRepository.buscarParaRegistro(eventoId)
                .orElseThrow(() -> ResourceNotFoundException.de("evento", eventoId));
        Asistente asistente = asistenteRepository.findById(dto.getAsistenteId())
                .orElseThrow(() -> ResourceNotFoundException.de("asistente", dto.getAsistenteId()));

        LocalDateTime ahora = LocalDateTime.now();
        if (evento.getEstado() != EstadoEvento.PUBLICADO) {
            throw new ConflictException("El evento no esta disponible para registro");
        }
        if (evento.yaSeRealizo(ahora)) {
            throw new ConflictException("El evento ya se realizo");
        }
        if (registroRepository.existsByEvento_IdAndAsistente_IdAndEstado(
                eventoId, asistente.getId(), EstadoRegistro.ACTIVO)) {
            throw new ConflictException("El asistente ya esta registrado en este evento");
        }

        long inscritos = contarActivos(eventoId);
        long disponibles = cuposDisponibles(evento, inscritos);
        if (disponibles <= 0) {
            throw new ConflictException("El evento alcanzo su capacidad maxima");
        }

        Registro registro = Registro.builder()
                .evento(evento)
                .asistente(asistente)
                .estado(EstadoRegistro.ACTIVO)
                .fechaRegistro(ahora)
                .build();
        registroRepository.save(registro);
        return RegistroMapper.aRespuesta(registro, disponibles - 1);
    }

    /** CU-15 y RN-11: cancela el registro y libera un cupo. */
    @Transactional
    public RegistroResponseDTO cancelar(Long registroId) {
        Registro registro = buscar(registroId);
        if (!registro.estaActivo()) {
            throw new ConflictException("El registro ya fue cancelado anteriormente");
        }
        if (registro.getEvento().yaSeRealizo(LocalDateTime.now())) {
            throw new ConflictException("No se puede cancelar un registro de un evento finalizado");
        }
        registro.cancelar();
        registroRepository.save(registro);

        long inscritos = contarActivos(registro.getEvento().getId());
        return RegistroMapper.aRespuesta(registro, cuposDisponibles(registro.getEvento(), inscritos));
    }

    /** Solo se eliminan registros cancelados, para no perder la trazabilidad de los activos. */
    @Transactional
    public void eliminar(Long registroId) {
        Registro registro = buscar(registroId);
        if (registro.estaActivo()) {
            throw new ConflictException("Solo se pueden eliminar registros cancelados");
        }
        registroRepository.delete(registro);
    }

    /** CU-16: capacidad maxima, inscritos y cupos disponibles. */
    @Transactional(readOnly = true)
    public DisponibilidadResponseDTO disponibilidad(Long eventoId) {
        Evento evento = buscarEvento(eventoId);
        long inscritos = contarActivos(eventoId);
        return DisponibilidadResponseDTO.builder()
                .eventoId(evento.getId())
                .evento(evento.getNombre())
                .capacidadMaxima(evento.getCapacidadMaxima())
                .inscritos(inscritos)
                .disponibles(cuposDisponibles(evento, inscritos))
                .build();
    }

    /** CU-17: asistentes con registro ACTIVO en el evento. */
    @Transactional(readOnly = true)
    public List<AsistenteEventoDTO> asistentesDeEvento(Long eventoId) {
        buscarEvento(eventoId);
        return registroRepository
                .findByEvento_IdAndEstadoOrderByFechaRegistroAsc(eventoId, EstadoRegistro.ACTIVO)
                .stream()
                .map(registro -> AsistenteEventoDTO.builder()
                        .registroId(registro.getId())
                        .asistente(AsistenteMapper.aResumen(registro.getAsistente()))
                        .estado(registro.getEstado())
                        .fechaRegistro(registro.getFechaRegistro())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistroResponseDTO obtener(Long registroId) {
        Registro registro = buscar(registroId);
        long inscritos = contarActivos(registro.getEvento().getId());
        return RegistroMapper.aRespuesta(registro, cuposDisponibles(registro.getEvento(), inscritos));
    }

    private Registro buscar(Long registroId) {
        return registroRepository.findById(registroId)
                .orElseThrow(() -> ResourceNotFoundException.de("registro", registroId));
    }

    private Evento buscarEvento(Long eventoId) {
        return eventoRepository.findById(eventoId)
                .orElseThrow(() -> ResourceNotFoundException.de("evento", eventoId));
    }

    private long contarActivos(Long eventoId) {
        return registroRepository.countByEvento_IdAndEstado(eventoId, EstadoRegistro.ACTIVO);
    }

    private long cuposDisponibles(Evento evento, long inscritos) {
        return Math.max(evento.getCapacidadMaxima() - inscritos, 0);
    }
}
