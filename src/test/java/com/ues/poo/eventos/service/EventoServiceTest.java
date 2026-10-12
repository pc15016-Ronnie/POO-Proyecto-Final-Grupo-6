package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.EventoRequestDTO;
import com.ues.poo.eventos.exception.BadRequestException;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.model.EstadoEvento;
import com.ues.poo.eventos.model.EstadoRegistro;
import com.ues.poo.eventos.model.Evento;
import com.ues.poo.eventos.model.Ubicacion;
import com.ues.poo.eventos.repository.EventoRepository;
import com.ues.poo.eventos.repository.RegistroRepository;
import com.ues.poo.eventos.repository.UbicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de las reglas de negocio del evento (RN-01, RN-05, RN-06, RN-10 y RN-12). */
@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private UbicacionRepository ubicacionRepository;
    @Mock
    private RegistroRepository registroRepository;

    @InjectMocks
    private EventoService eventoService;

    private Ubicacion ubicacion;

    @BeforeEach
    void prepararDatos() {
        ubicacion = Ubicacion.builder().id(1L).nombre("Auditorio Central").capacidad(200).build();
    }

    @Test
    @DisplayName("CU-01: crea el evento cuando la ubicacion existe y la fecha es futura")
    void creaEvento() {
        EventoRequestDTO dto = new EventoRequestDTO(1L, "Conferencia", "Charla general",
                LocalDateTime.now().plusDays(5), null, 50, EstadoEvento.BORRADOR);
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacion));
        when(eventoRepository.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(registroRepository.countByEvento_IdAndEstado(any(), any())).thenReturn(0L);

        var respuesta = eventoService.crear(dto);

        assertThat(respuesta.getNombre()).isEqualTo("Conferencia");
        assertThat(respuesta.getEstado()).isEqualTo(EstadoEvento.BORRADOR);
        assertThat(respuesta.getCuposDisponibles()).isEqualTo(50);
    }

    @Test
    @DisplayName("CU-01: rechaza un evento con fecha pasada")
    void rechazaFechaPasada() {
        EventoRequestDTO dto = new EventoRequestDTO(1L, "Conferencia", null,
                LocalDateTime.now().minusDays(1), null, 50, EstadoEvento.BORRADOR);

        assertThatThrownBy(() -> eventoService.crear(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("futura");

        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("CU-01: responde 404 cuando la ubicacion indicada no existe")
    void rechazaUbicacionInexistente() {
        EventoRequestDTO dto = new EventoRequestDTO(99L, "Conferencia", null,
                LocalDateTime.now().plusDays(5), null, 50, EstadoEvento.BORRADOR);
        when(ubicacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventoService.crear(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("RN-05: no permite reducir la capacidad por debajo de los inscritos")
    void rechazaCapacidadMenorAInscritos() {
        Evento evento = Evento.builder()
                .id(10L)
                .ubicacion(ubicacion)
                .nombre("Taller")
                .fechaInicio(LocalDateTime.now().plusDays(3))
                .capacidadMaxima(10)
                .estado(EstadoEvento.PUBLICADO)
                .build();
        EventoRequestDTO dto = new EventoRequestDTO(1L, "Taller", null,
                LocalDateTime.now().plusDays(3), null, 2, EstadoEvento.PUBLICADO);

        when(eventoRepository.findById(10L)).thenReturn(Optional.of(evento));
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacion));
        when(registroRepository.countByEvento_IdAndEstado(10L, EstadoRegistro.ACTIVO)).thenReturn(5L);

        assertThatThrownBy(() -> eventoService.actualizar(10L, dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("no puede ser menor");
    }

    @Test
    @DisplayName("RN-06: no permite eliminar un evento con asistentes inscritos")
    void rechazaEliminarConInscritos() {
        Evento evento = Evento.builder()
                .id(10L)
                .ubicacion(ubicacion)
                .nombre("Taller")
                .fechaInicio(LocalDateTime.now().plusDays(3))
                .capacidadMaxima(10)
                .estado(EstadoEvento.PUBLICADO)
                .build();
        when(eventoRepository.findById(10L)).thenReturn(Optional.of(evento));
        when(registroRepository.countByEvento_IdAndEstado(10L, EstadoRegistro.ACTIVO)).thenReturn(2L);

        assertThatThrownBy(() -> eventoService.eliminar(10L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("asistentes inscritos");

        verify(eventoRepository, never()).delete(any(Evento.class));
    }
}
