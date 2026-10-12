package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.RegistroRequestDTO;
import com.ues.poo.eventos.dto.RegistroResponseDTO;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.model.Asistente;
import com.ues.poo.eventos.model.EstadoEvento;
import com.ues.poo.eventos.model.EstadoRegistro;
import com.ues.poo.eventos.model.Evento;
import com.ues.poo.eventos.model.Registro;
import com.ues.poo.eventos.model.Ubicacion;
import com.ues.poo.eventos.repository.AsistenteRepository;
import com.ues.poo.eventos.repository.EventoRepository;
import com.ues.poo.eventos.repository.RegistroRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de las reglas de aforo, registro unico y cancelacion. */
@ExtendWith(MockitoExtension.class)
class RegistroServiceTest {

    @Mock
    private RegistroRepository registroRepository;
    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private AsistenteRepository asistenteRepository;

    @InjectMocks
    private RegistroService registroService;

    private Evento eventoPublicado;
    private Asistente asistente;

    @BeforeEach
    void prepararDatos() {
        Ubicacion ubicacion = Ubicacion.builder().id(1L).nombre("Auditorio").capacidad(100).build();

        eventoPublicado = Evento.builder()
                .id(10L)
                .nombre("Taller de Spring Boot")
                .ubicacion(ubicacion)
                .fechaInicio(LocalDateTime.now().plusDays(10))
                .capacidadMaxima(3)
                .estado(EstadoEvento.PUBLICADO)
                .build();

        asistente = new Asistente();
        asistente.setId(5L);
        asistente.setNombre("Azucena");
        asistente.setApellido("Castillo");
        asistente.setEmail("azucena@ues.edu.sv");
    }

    @Test
    @DisplayName("CU-14: registra al asistente cuando hay cupo disponible")
    void registraCuandoHayCupo() {
        when(eventoRepository.buscarParaRegistro(10L)).thenReturn(Optional.of(eventoPublicado));
        when(asistenteRepository.findById(5L)).thenReturn(Optional.of(asistente));
        when(registroRepository.existsByEvento_IdAndAsistente_IdAndEstado(10L, 5L, EstadoRegistro.ACTIVO))
                .thenReturn(false);
        when(registroRepository.countByEvento_IdAndEstado(10L, EstadoRegistro.ACTIVO)).thenReturn(2L);
        when(registroRepository.save(any(Registro.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistroResponseDTO respuesta = registroService.registrar(10L, new RegistroRequestDTO(5L));

        assertThat(respuesta.getEstado()).isEqualTo(EstadoRegistro.ACTIVO);
        assertThat(respuesta.getCuposDisponibles()).isZero();
        assertThat(respuesta.getAsistente().getEmail()).isEqualTo("azucena@ues.edu.sv");
    }

    @Test
    @DisplayName("RN-03: rechaza el registro cuando el evento alcanzo su capacidad maxima")
    void rechazaCuandoNoHayCupo() {
        when(eventoRepository.buscarParaRegistro(10L)).thenReturn(Optional.of(eventoPublicado));
        when(asistenteRepository.findById(5L)).thenReturn(Optional.of(asistente));
        when(registroRepository.existsByEvento_IdAndAsistente_IdAndEstado(10L, 5L, EstadoRegistro.ACTIVO))
                .thenReturn(false);
        when(registroRepository.countByEvento_IdAndEstado(10L, EstadoRegistro.ACTIVO)).thenReturn(3L);

        assertThatThrownBy(() -> registroService.registrar(10L, new RegistroRequestDTO(5L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("capacidad maxima");

        verify(registroRepository, never()).save(any(Registro.class));
    }

    @Test
    @DisplayName("RN-04: rechaza el registro duplicado del mismo asistente")
    void rechazaRegistroDuplicado() {
        when(eventoRepository.buscarParaRegistro(10L)).thenReturn(Optional.of(eventoPublicado));
        when(asistenteRepository.findById(5L)).thenReturn(Optional.of(asistente));
        when(registroRepository.existsByEvento_IdAndAsistente_IdAndEstado(10L, 5L, EstadoRegistro.ACTIVO))
                .thenReturn(true);

        assertThatThrownBy(() -> registroService.registrar(10L, new RegistroRequestDTO(5L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ya esta registrado");
    }

    @Test
    @DisplayName("RN-10: rechaza el registro en un evento que no esta publicado")
    void rechazaEventoNoPublicado() {
        eventoPublicado.setEstado(EstadoEvento.BORRADOR);
        when(eventoRepository.buscarParaRegistro(10L)).thenReturn(Optional.of(eventoPublicado));
        when(asistenteRepository.findById(5L)).thenReturn(Optional.of(asistente));

        assertThatThrownBy(() -> registroService.registrar(10L, new RegistroRequestDTO(5L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("no esta disponible");
    }

    @Test
    @DisplayName("CU-14: responde 404 cuando el evento no existe")
    void eventoInexistente() {
        when(eventoRepository.buscarParaRegistro(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registroService.registrar(99L, new RegistroRequestDTO(5L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("CU-15 y RN-11: al cancelar se marca CANCELADO y se libera el cupo")
    void cancelarLiberaCupo() {
        Registro registro = Registro.builder()
                .id(7L)
                .evento(eventoPublicado)
                .asistente(asistente)
                .estado(EstadoRegistro.ACTIVO)
                .fechaRegistro(LocalDateTime.now().minusDays(1))
                .build();
        when(registroRepository.findById(7L)).thenReturn(Optional.of(registro));
        when(registroRepository.save(any(Registro.class))).thenAnswer(inv -> inv.getArgument(0));
        when(registroRepository.countByEvento_IdAndEstado(eq(10L), eq(EstadoRegistro.ACTIVO))).thenReturn(1L);

        RegistroResponseDTO respuesta = registroService.cancelar(7L);

        assertThat(respuesta.getEstado()).isEqualTo(EstadoRegistro.CANCELADO);
        assertThat(respuesta.getFechaCancelacion()).isNotNull();
        assertThat(respuesta.getCuposDisponibles()).isEqualTo(2);
    }

    @Test
    @DisplayName("CU-15: no permite cancelar dos veces el mismo registro")
    void noPermiteCancelarDosVeces() {
        Registro registro = Registro.builder()
                .id(7L)
                .evento(eventoPublicado)
                .asistente(asistente)
                .estado(EstadoRegistro.CANCELADO)
                .fechaRegistro(LocalDateTime.now().minusDays(2))
                .fechaCancelacion(LocalDateTime.now().minusDays(1))
                .build();
        when(registroRepository.findById(7L)).thenReturn(Optional.of(registro));

        assertThatThrownBy(() -> registroService.cancelar(7L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ya fue cancelado");
    }

    @Test
    @DisplayName("CU-16: la disponibilidad se calcula como capacidad menos registros activos")
    void calculaDisponibilidad() {
        when(eventoRepository.findById(10L)).thenReturn(Optional.of(eventoPublicado));
        when(registroRepository.countByEvento_IdAndEstado(10L, EstadoRegistro.ACTIVO)).thenReturn(1L);

        var disponibilidad = registroService.disponibilidad(10L);

        assertThat(disponibilidad.getCapacidadMaxima()).isEqualTo(3);
        assertThat(disponibilidad.getInscritos()).isEqualTo(1);
        assertThat(disponibilidad.getDisponibles()).isEqualTo(2);
    }
}
