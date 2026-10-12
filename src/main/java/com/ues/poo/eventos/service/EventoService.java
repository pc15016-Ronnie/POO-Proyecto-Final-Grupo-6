package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.EventoRequestDTO;
import com.ues.poo.eventos.dto.EventoResponseDTO;
import com.ues.poo.eventos.exception.BadRequestException;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.mapper.EventoMapper;
import com.ues.poo.eventos.model.EstadoEvento;
import com.ues.poo.eventos.model.EstadoRegistro;
import com.ues.poo.eventos.model.Evento;
import com.ues.poo.eventos.model.Ubicacion;
import com.ues.poo.eventos.repository.EventoRepository;
import com.ues.poo.eventos.repository.RegistroRepository;
import com.ues.poo.eventos.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** CU-01 a CU-04 y CU-13: gestion de eventos y de la regla de aforo. */
@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final RegistroRepository registroRepository;

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listar(EstadoEvento estado, Long ubicacionId,
                                          LocalDateTime desde, LocalDateTime hasta) {
        List<Specification<Evento>> filtros = new ArrayList<>();
        if (estado != null) {
            filtros.add((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }
        if (ubicacionId != null) {
            filtros.add((root, query, cb) -> cb.equal(root.get("ubicacion").get("id"), ubicacionId));
        }
        if (desde != null) {
            filtros.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.<LocalDateTime>get("fechaInicio"), desde));
        }
        if (hasta != null) {
            filtros.add((root, query, cb) -> cb.lessThanOrEqualTo(root.<LocalDateTime>get("fechaInicio"), hasta));
        }
        return eventoRepository
                .findAll(Specification.allOf(filtros), Sort.by("fechaInicio").ascending())
                .stream()
                .map(this::aRespuestaConAforo)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponseDTO obtener(Long id) {
        return aRespuestaConAforo(buscar(id));
    }

    /** CU-13: eventos publicados, con fecha futura y con cupo disponible. */
    @Transactional(readOnly = true)
    public List<EventoResponseDTO> disponibles() {
        return eventoRepository
                .findByEstadoAndFechaInicioAfterOrderByFechaInicioAsc(EstadoEvento.PUBLICADO, LocalDateTime.now())
                .stream()
                .map(this::aRespuestaConAforo)
                .filter(dto -> dto.getCuposDisponibles() != null && dto.getCuposDisponibles() > 0)
                .toList();
    }

    @Transactional
    public EventoResponseDTO crear(EventoRequestDTO dto) {
        validarFechas(dto);
        if (!dto.getFechaInicio().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("La fecha del evento debe ser futura");
        }
        Ubicacion ubicacion = buscarUbicacion(dto.getUbicacionId());
        Evento evento = EventoMapper.aEntidad(dto, ubicacion);
        return aRespuestaConAforo(eventoRepository.save(evento));
    }

    /** CU-03: al reducir la capacidad se aplica RN-05. */
    @Transactional
    public EventoResponseDTO actualizar(Long id, EventoRequestDTO dto) {
        validarFechas(dto);
        Evento evento = buscar(id);
        Ubicacion ubicacion = buscarUbicacion(dto.getUbicacionId());

        long inscritos = inscritosActivos(id);
        if (dto.getCapacidadMaxima() < inscritos) {
            throw new ConflictException("La capacidad no puede ser menor que el numero de inscritos ("
                    + inscritos + ")");
        }

        EventoMapper.copiarDatos(evento, dto, ubicacion);
        return aRespuestaConAforo(eventoRepository.save(evento));
    }

    /** RN-06: no se puede eliminar un evento que tenga registros activos. */
    @Transactional
    public void eliminar(Long id) {
        Evento evento = buscar(id);
        if (inscritosActivos(id) > 0) {
            throw new ConflictException("No se puede eliminar un evento con asistentes inscritos");
        }
        registroRepository.deleteByEvento_Id(id);
        eventoRepository.delete(evento);
    }

    @Transactional(readOnly = true)
    public long inscritosActivos(Long eventoId) {
        return registroRepository.countByEvento_IdAndEstado(eventoId, EstadoRegistro.ACTIVO);
    }

    Evento buscar(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("evento", id));
    }

    private EventoResponseDTO aRespuestaConAforo(Evento evento) {
        return EventoMapper.aRespuesta(evento, inscritosActivos(evento.getId()));
    }

    private Ubicacion buscarUbicacion(Long ubicacionId) {
        return ubicacionRepository.findById(ubicacionId)
                .orElseThrow(() -> ResourceNotFoundException.de("ubicacion", ubicacionId));
    }

    private void validarFechas(EventoRequestDTO dto) {
        if (dto.getFechaFin() != null && dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }
}
