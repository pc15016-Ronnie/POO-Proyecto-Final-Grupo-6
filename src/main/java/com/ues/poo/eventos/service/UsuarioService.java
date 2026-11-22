package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.UsuarioRequestDTO;
import com.ues.poo.eventos.dto.UsuarioResponseDTO;
import com.ues.poo.eventos.exception.BadRequestException;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.mapper.UsuarioMapper;
import com.ues.poo.eventos.model.Usuario;
import com.ues.poo.eventos.repository.PersonaRepository;
import com.ues.poo.eventos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/** CRUD administrativo de credenciales y de su relacion opcional con Persona. */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll(Sort.by("username").ascending()).stream()
                .map(UsuarioMapper::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtener(Long id) {
        return UsuarioMapper.aRespuesta(buscar(id));
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        String username = normalizarUsername(dto.getUsername());
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("El nombre de usuario ya esta registrado");
        }
        Usuario usuario = new Usuario();
        copiarDatos(usuario, dto, username);
        return UsuarioMapper.aRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscar(id);
        String username = normalizarUsername(dto.getUsername());
        if (usuarioRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new ConflictException("El nombre de usuario ya esta registrado por otro usuario");
        }
        if (usuario.getRol() != dto.getRol() && personaRepository.existsByUsuario_Id(id)) {
            throw new BadRequestException(
                    "No se puede cambiar el rol de un usuario que tiene un perfil asociado");
        }
        copiarDatos(usuario, dto, username);
        return UsuarioMapper.aRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscar(id);
        if (personaRepository.existsByUsuario_Id(id)) {
            throw new ConflictException("No se puede eliminar un usuario que tiene un perfil asociado");
        }
        usuarioRepository.delete(usuario);
    }

    Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("usuario", id));
    }

    private void copiarDatos(Usuario usuario, UsuarioRequestDTO dto, String username) {
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol());
        usuario.setActivo(dto.getActivo() == null ? Boolean.TRUE : dto.getActivo());
    }

    private String normalizarUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
