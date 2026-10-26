package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.AsistenteRequestDTO;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.model.Asistente;
import com.ues.poo.eventos.model.EstadoRegistro;
import com.ues.poo.eventos.repository.AsistenteRepository;
import com.ues.poo.eventos.repository.RegistroRepository;
import com.ues.poo.eventos.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de las reglas del asistente (RN-08 y RN-09). */
@ExtendWith(MockitoExtension.class)
class AsistenteServiceTest {

    @Mock
    private AsistenteRepository asistenteRepository;
    @Mock
    private RegistroRepository registroRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AsistenteService asistenteService;

    private AsistenteRequestDTO dto() {
        return new AsistenteRequestDTO("Maria", "Lopez", "Maria.Lopez@ues.edu.sv", "7777-0000", null);
    }

    @Test
    @DisplayName("CU-09: crea el asistente normalizando el correo a minusculas")
    void creaAsistente() {
        when(asistenteRepository.existsByEmailIgnoreCase("maria.lopez@ues.edu.sv")).thenReturn(false);
        when(asistenteRepository.save(any(Asistente.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = asistenteService.crear(dto());

        assertThat(respuesta.getEmail()).isEqualTo("maria.lopez@ues.edu.sv");
        assertThat(respuesta.getNombre()).isEqualTo("Maria");
    }

    @Test
    @DisplayName("RN-09: rechaza un correo electronico repetido")
    void rechazaCorreoDuplicado() {
        when(asistenteRepository.existsByEmailIgnoreCase("maria.lopez@ues.edu.sv")).thenReturn(true);

        assertThatThrownBy(() -> asistenteService.crear(dto()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("correo electronico ya esta registrado");

        verify(asistenteRepository, never()).save(any(Asistente.class));
    }

    @Test
    @DisplayName("RN-08: no permite eliminar un asistente con registros activos")
    void rechazaEliminarConRegistrosActivos() {
        Asistente asistente = new Asistente();
        asistente.setId(5L);
        when(asistenteRepository.findById(5L)).thenReturn(Optional.of(asistente));
        when(registroRepository.countByAsistente_IdAndEstado(5L, EstadoRegistro.ACTIVO)).thenReturn(1L);

        assertThatThrownBy(() -> asistenteService.eliminar(5L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("registros activos");

        verify(asistenteRepository, never()).delete(any(Asistente.class));
    }
}
