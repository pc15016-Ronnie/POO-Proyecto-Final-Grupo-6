package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.AdministradorRequestDTO;
import com.ues.poo.eventos.dto.AdministradorResponseDTO;
import com.ues.poo.eventos.exception.BadRequestException;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.mapper.AdministradorMapper;
import com.ues.poo.eventos.model.Administrador;
import com.ues.poo.eventos.model.RolUsuario;
import com.ues.poo.eventos.model.Usuario;
import com.ues.poo.eventos.repository.AdministradorRepository;
import com.ues.poo.eventos.repository.PersonaRepository;
import com.ues.poo.eventos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/** CRUD del perfil Administrador y su vinculo con credenciales. */
@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<AdministradorResponseDTO> listar() {
        return administradorRepository.findAll(
                        Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()))
                .stream()
                .map(AdministradorMapper::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdministradorResponseDTO obtener(Long id) {
        return AdministradorMapper.aRespuesta(buscar(id));
    }

    @Transactional
    public AdministradorResponseDTO crear(AdministradorRequestDTO dto) {
        String email = normalizarEmail(dto.getEmail());
        if (personaRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("El correo electronico ya esta registrado");
        }
        Administrador administrador = AdministradorMapper.aEntidad(dto);
        administrador.setUsuario(resolverUsuarioAdministrador(dto.getUsuarioId(), null));
        return AdministradorMapper.aRespuesta(administradorRepository.save(administrador));
    }

    @Transactional
    public AdministradorResponseDTO actualizar(Long id, AdministradorRequestDTO dto) {
        Administrador administrador = buscar(id);
        String email = normalizarEmail(dto.getEmail());
        if (personaRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictException("El correo electronico ya esta registrado por otra persona");
        }
        AdministradorMapper.copiarDatos(administrador, dto);
        administrador.setUsuario(resolverUsuarioAdministrador(dto.getUsuarioId(), id));
        return AdministradorMapper.aRespuesta(administradorRepository.save(administrador));
    }

    @Transactional
    public void eliminar(Long id) {
        administradorRepository.delete(buscar(id));
    }

    Administrador buscar(Long id) {
        return administradorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("administrador", id));
    }

    private Usuario resolverUsuarioAdministrador(Long usuarioId, Long personaId) {
        if (usuarioId == null) {
            return null;
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.de("usuario", usuarioId));
        if (usuario.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new BadRequestException("El usuario vinculado debe tener rol ADMINISTRADOR");
        }
        boolean vinculado = personaId == null
                ? personaRepository.existsByUsuario_Id(usuarioId)
                : personaRepository.existsByUsuario_IdAndIdNot(usuarioId, personaId);
        if (vinculado) {
            throw new ConflictException("El usuario ya esta vinculado a otro perfil");
        }
        return usuario;
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
