package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.AdministradorRequestDTO;
import com.ues.poo.eventos.exception.BadRequestException;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.model.Administrador;
import com.ues.poo.eventos.model.RolUsuario;
import com.ues.poo.eventos.model.Usuario;
import com.ues.poo.eventos.repository.AdministradorRepository;
import com.ues.poo.eventos.repository.PersonaRepository;
import com.ues.poo.eventos.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas del CRUD de administradores y de la relacion persona-usuario. */
@ExtendWith(MockitoExtension.class)
class AdministradorServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AdministradorService administradorService;

    private AdministradorRequestDTO dto() {
        return new AdministradorRequestDTO(
                " Erick ", " Trigueros ", "Admin.Nuevo@UES.EDU.SV ",
                "7777-1111", "Ingenieria", 1L);
    }

    @Test
    @DisplayName("CU-24: crea y vincula un administrador con un usuario ADMINISTRADOR")
    void creaAdministrador() {
        Usuario usuario = usuario(1L, RolUsuario.ADMINISTRADOR);
        when(personaRepository.existsByEmailIgnoreCase("admin.nuevo@ues.edu.sv")).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(personaRepository.existsByUsuario_Id(1L)).thenReturn(false);
        when(administradorRepository.save(any(Administrador.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = administradorService.crear(dto());

        assertThat(respuesta.getEmail()).isEqualTo("admin.nuevo@ues.edu.sv");
        assertThat(respuesta.getNombre()).isEqualTo("Erick");
        assertThat(respuesta.getUsuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("CU-24: rechaza un correo ya usado por cualquier persona")
    void rechazaCorreoDuplicadoEntrePersonas() {
        when(personaRepository.existsByEmailIgnoreCase("admin.nuevo@ues.edu.sv")).thenReturn(true);

        assertThatThrownBy(() -> administradorService.crear(dto()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("correo electronico");

        verify(administradorRepository, never()).save(any(Administrador.class));
    }

    @Test
    @DisplayName("CU-25: lista y consulta administradores")
    void listaYObtieneAdministrador() {
        Administrador administrador = administrador(1L, "Erick", "Trigueros");
        when(administradorRepository.findAll(any(Sort.class))).thenReturn(List.of(administrador));
        when(administradorRepository.findById(1L)).thenReturn(Optional.of(administrador));

        var lista = administradorService.listar();
        var detalle = administradorService.obtener(1L);

        assertThat(lista).hasSize(1);
        assertThat(detalle.getApellido()).isEqualTo("Trigueros");
    }

    @Test
    @DisplayName("CU-26: actualiza datos y desvincula el usuario cuando usuarioId es nulo")
    void actualizaAdministrador() {
        Administrador administrador = administrador(1L, "Anterior", "Perfil");
        administrador.setUsuario(usuario(1L, RolUsuario.ADMINISTRADOR));
        AdministradorRequestDTO dto = new AdministradorRequestDTO(
                "Erick", "Trigueros", "erick@ues.edu.sv", null, "Arquitectura", null);
        when(administradorRepository.findById(1L)).thenReturn(Optional.of(administrador));
        when(personaRepository.existsByEmailIgnoreCaseAndIdNot("erick@ues.edu.sv", 1L)).thenReturn(false);
        when(administradorRepository.save(any(Administrador.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = administradorService.actualizar(1L, dto);

        assertThat(respuesta.getDepartamento()).isEqualTo("Arquitectura");
        assertThat(respuesta.getUsuarioId()).isNull();
    }

    @Test
    @DisplayName("CU-26: rechaza vincular un usuario con rol ASISTENTE")
    void rechazaUsuarioConRolIncompatible() {
        when(personaRepository.existsByEmailIgnoreCase("admin.nuevo@ues.edu.sv")).thenReturn(false);
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario(1L, RolUsuario.ASISTENTE)));

        assertThatThrownBy(() -> administradorService.crear(dto()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ADMINISTRADOR");
    }

    @Test
    @DisplayName("CU-27: elimina el perfil administrador y conserva sus credenciales")
    void eliminaAdministrador() {
        Administrador administrador = administrador(1L, "Erick", "Trigueros");
        when(administradorRepository.findById(1L)).thenReturn(Optional.of(administrador));

        administradorService.eliminar(1L);

        verify(administradorRepository).delete(administrador);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    private Administrador administrador(Long id, String nombre, String apellido) {
        Administrador administrador = new Administrador();
        administrador.setId(id);
        administrador.setNombre(nombre);
        administrador.setApellido(apellido);
        administrador.setEmail("perfil" + id + "@ues.edu.sv");
        administrador.setDepartamento("Ingenieria");
        return administrador;
    }

    private Usuario usuario(Long id, RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername("usuario" + id);
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuario;
    }
}
