package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.AsistenteRequestDTO;
import com.ues.poo.eventos.exception.BadRequestException;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.model.Asistente;
import com.ues.poo.eventos.model.EstadoRegistro;
import com.ues.poo.eventos.model.RolUsuario;
import com.ues.poo.eventos.model.Usuario;
import com.ues.poo.eventos.repository.AsistenteRepository;
import com.ues.poo.eventos.repository.PersonaRepository;
import com.ues.poo.eventos.repository.RegistroRepository;
import com.ues.poo.eventos.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private AsistenteService asistenteService;

    private AsistenteRequestDTO dto() {
        return new AsistenteRequestDTO("Maria", "Lopez", "Maria.Lopez@ues.edu.sv", "7777-0000", null);
    }

    @Test
    @DisplayName("CU-09: crea el asistente normalizando el correo a minusculas")
    void creaAsistente() {
        when(personaRepository.existsByEmailIgnoreCase("maria.lopez@ues.edu.sv")).thenReturn(false);
        when(asistenteRepository.save(any(Asistente.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = asistenteService.crear(dto());

        assertThat(respuesta.getEmail()).isEqualTo("maria.lopez@ues.edu.sv");
        assertThat(respuesta.getNombre()).isEqualTo("Maria");
    }

    @Test
    @DisplayName("RN-09: rechaza un correo electronico repetido")
    void rechazaCorreoDuplicado() {
        when(personaRepository.existsByEmailIgnoreCase("maria.lopez@ues.edu.sv")).thenReturn(true);

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

    @Test
    @DisplayName("CU-10: lista asistentes ordenados por apellido y nombre")
    void listaAsistentes() {
        Asistente asistente = new Asistente();
        asistente.setId(5L);
        asistente.setNombre("Maria");
        asistente.setApellido("Lopez");
        asistente.setEmail("maria.lopez@ues.edu.sv");
        when(asistenteRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(asistente));

        var respuesta = asistenteService.listar();

        assertThat(respuesta).hasSize(1);
        assertThat(respuesta.getFirst().getEmail()).isEqualTo("maria.lopez@ues.edu.sv");
    }

    @Test
    @DisplayName("CU-10: responde 404 cuando el asistente no existe")
    void asistenteInexistente() {
        when(asistenteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> asistenteService.obtener(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("CU-11: actualiza correo y vincula un usuario con rol ASISTENTE")
    void actualizaAsistenteYUsuario() {
        Asistente asistente = new Asistente();
        asistente.setId(5L);
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setRol(RolUsuario.ASISTENTE);
        AsistenteRequestDTO dto = new AsistenteRequestDTO(
                " Maria ", " Lopez ", " NUEVO@UES.EDU.SV ", "7000-1111", 2L);

        when(asistenteRepository.findById(5L)).thenReturn(Optional.of(asistente));
        when(personaRepository.existsByEmailIgnoreCaseAndIdNot("nuevo@ues.edu.sv", 5L)).thenReturn(false);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(personaRepository.existsByUsuario_IdAndIdNot(2L, 5L)).thenReturn(false);
        when(asistenteRepository.save(any(Asistente.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = asistenteService.actualizar(5L, dto);

        assertThat(respuesta.getEmail()).isEqualTo("nuevo@ues.edu.sv");
        assertThat(respuesta.getNombre()).isEqualTo("Maria");
        assertThat(respuesta.getUsuarioId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("CU-11: rechaza vincular un usuario que no tiene rol ASISTENTE")
    void rechazaUsuarioConRolIncompatible() {
        AsistenteRequestDTO dto = new AsistenteRequestDTO(
                "Maria", "Lopez", "maria.lopez@ues.edu.sv", null, 1L);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(RolUsuario.ADMINISTRADOR);
        when(personaRepository.existsByEmailIgnoreCase("maria.lopez@ues.edu.sv")).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> asistenteService.crear(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ASISTENTE");
    }
}
