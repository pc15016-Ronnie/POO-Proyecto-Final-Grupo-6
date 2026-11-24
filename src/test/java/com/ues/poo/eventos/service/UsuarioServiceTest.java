package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.UsuarioRequestDTO;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.model.RolUsuario;
import com.ues.poo.eventos.model.Usuario;
import com.ues.poo.eventos.repository.PersonaRepository;
import com.ues.poo.eventos.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas del CRUD de credenciales y de sus restricciones de rol. */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO dto() {
        return new UsuarioRequestDTO("  Admin.Nuevo  ", "secreto123", RolUsuario.ADMINISTRADOR, true);
    }

    @Test
    @DisplayName("CU-20: crea un usuario normalizando username y codificando la contrasena")
    void creaUsuario() {
        when(usuarioRepository.existsByUsernameIgnoreCase("admin.nuevo")).thenReturn(false);
        when(passwordEncoder.encode("secreto123")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = usuarioService.crear(dto());

        assertThat(respuesta.getUsername()).isEqualTo("admin.nuevo");
        assertThat(respuesta.getRol()).isEqualTo(RolUsuario.ADMINISTRADOR);
        verify(passwordEncoder).encode("secreto123");
    }

    @Test
    @DisplayName("CU-20: rechaza un username duplicado sin distinguir mayusculas")
    void rechazaUsernameDuplicado() {
        when(usuarioRepository.existsByUsernameIgnoreCase("admin.nuevo")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crear(dto()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("nombre de usuario");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("CU-21: lista y consulta usuarios sin exponer passwordHash")
    void listaYObtieneUsuario() {
        Usuario usuario = usuario(1L, "admin", RolUsuario.ADMINISTRADOR);
        when(usuarioRepository.findAll(any(Sort.class))).thenReturn(List.of(usuario));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        var lista = usuarioService.listar();
        var detalle = usuarioService.obtener(1L);

        assertThat(lista).hasSize(1);
        assertThat(detalle.getUsername()).isEqualTo("admin");
        assertThat(detalle.getRol()).isEqualTo(RolUsuario.ADMINISTRADOR);
    }

    @Test
    @DisplayName("CU-22: actualiza credenciales de un usuario sin perfil asociado")
    void actualizaUsuario() {
        Usuario usuario = usuario(1L, "admin", RolUsuario.ADMINISTRADOR);
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Asistente.Nuevo", "nuevaClave9", RolUsuario.ASISTENTE, false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByUsernameIgnoreCaseAndIdNot("asistente.nuevo", 1L))
                .thenReturn(false);
        when(personaRepository.existsByUsuario_Id(1L)).thenReturn(false);
        when(passwordEncoder.encode("nuevaClave9")).thenReturn("$2a$nuevo");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = usuarioService.actualizar(1L, dto);

        assertThat(respuesta.getUsername()).isEqualTo("asistente.nuevo");
        assertThat(respuesta.getRol()).isEqualTo(RolUsuario.ASISTENTE);
        assertThat(respuesta.getActivo()).isFalse();
    }

    @Test
    @DisplayName("CU-23: no elimina un usuario que tiene un perfil vinculado")
    void rechazaEliminarUsuarioVinculado() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario(1L, "admin", RolUsuario.ADMINISTRADOR)));
        when(personaRepository.existsByUsuario_Id(1L)).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.eliminar(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("perfil asociado");

        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    @DisplayName("CU-23: elimina un usuario sin perfil vinculado")
    void eliminaUsuarioSinPerfil() {
        Usuario usuario = usuario(9L, "temporal", RolUsuario.ASISTENTE);
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(usuario));
        when(personaRepository.existsByUsuario_Id(9L)).thenReturn(false);

        usuarioService.eliminar(9L);

        verify(usuarioRepository).delete(usuario);
    }

    private Usuario usuario(Long id, String username, RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername(username);
        usuario.setPasswordHash("$2a$hash");
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuario;
    }
}
