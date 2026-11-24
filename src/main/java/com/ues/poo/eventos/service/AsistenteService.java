package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.AsistenteRequestDTO;
import com.ues.poo.eventos.dto.AsistenteResponseDTO;
import com.ues.poo.eventos.exception.BadRequestException;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.mapper.AsistenteMapper;
import com.ues.poo.eventos.model.Asistente;
import com.ues.poo.eventos.model.EstadoRegistro;
import com.ues.poo.eventos.model.RolUsuario;
import com.ues.poo.eventos.model.Usuario;
import com.ues.poo.eventos.repository.AsistenteRepository;
import com.ues.poo.eventos.repository.PersonaRepository;
import com.ues.poo.eventos.repository.RegistroRepository;
import com.ues.poo.eventos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CU-09 a CU-12: gestion del catalogo de asistentes. */
@Service
@RequiredArgsConstructor
public class AsistenteService {

    private final AsistenteRepository asistenteRepository;
    private final RegistroRepository registroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;

    @Transactional(readOnly = true)
    public List<AsistenteResponseDTO> listar() {
        return asistenteRepository.findAll(Sort.by("apellido").ascending().and(Sort.by("nombre"))).stream()
                .map(AsistenteMapper::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public AsistenteResponseDTO obtener(Long id) {
        return AsistenteMapper.aRespuesta(buscar(id));
    }

    /** RN-09: el correo electronico debe ser unico. */
    @Transactional
    public AsistenteResponseDTO crear(AsistenteRequestDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (personaRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("El correo electronico ya esta registrado");
        }
        Asistente asistente = AsistenteMapper.aEntidad(dto);
        asistente.setUsuario(resolverUsuario(dto.getUsuarioId(), null));
        return AsistenteMapper.aRespuesta(asistenteRepository.save(asistente));
    }

    @Transactional
    public AsistenteResponseDTO actualizar(Long id, AsistenteRequestDTO dto) {
        Asistente asistente = buscar(id);
        String email = dto.getEmail().trim().toLowerCase();
        if (personaRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictException("El correo electronico ya esta registrado por otra persona");
        }
        AsistenteMapper.copiarDatos(asistente, dto);
        asistente.setUsuario(resolverUsuario(dto.getUsuarioId(), id));
        return AsistenteMapper.aRespuesta(asistenteRepository.save(asistente));
    }

    /** RN-08: no se puede eliminar un asistente con registros activos. */
    @Transactional
    public void eliminar(Long id) {
        Asistente asistente = buscar(id);
        long activos = registroRepository.countByAsistente_IdAndEstado(id, EstadoRegistro.ACTIVO);
        if (activos > 0) {
            throw new ConflictException("No se puede eliminar un asistente con registros activos");
        }
        registroRepository.deleteByAsistente_Id(id);
        asistenteRepository.delete(asistente);
    }

    Asistente buscar(Long id) {
        return asistenteRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("asistente", id));
    }

    private Usuario resolverUsuario(Long usuarioId, Long personaId) {
        if (usuarioId == null) {
            return null;
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> ResourceNotFoundException.de("usuario", usuarioId));
        if (usuario.getRol() != RolUsuario.ASISTENTE) {
            throw new BadRequestException("El usuario vinculado debe tener rol ASISTENTE");
        }
        boolean vinculado = personaId == null
                ? personaRepository.existsByUsuario_Id(usuarioId)
                : personaRepository.existsByUsuario_IdAndIdNot(usuarioId, personaId);
        if (vinculado) {
            throw new ConflictException("El usuario ya esta vinculado a otro perfil");
        }
        return usuario;
    }
}
