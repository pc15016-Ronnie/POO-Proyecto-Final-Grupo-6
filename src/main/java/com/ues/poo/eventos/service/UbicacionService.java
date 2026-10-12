package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.UbicacionRequestDTO;
import com.ues.poo.eventos.dto.UbicacionResponseDTO;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.mapper.UbicacionMapper;
import com.ues.poo.eventos.model.Ubicacion;
import com.ues.poo.eventos.repository.EventoRepository;
import com.ues.poo.eventos.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CU-05 a CU-08: gestion del catalogo de ubicaciones. */
@Service
@RequiredArgsConstructor
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final EventoRepository eventoRepository;

    @Transactional(readOnly = true)
    public List<UbicacionResponseDTO> listar() {
        return ubicacionRepository.findAll(Sort.by("nombre").ascending()).stream()
                .map(UbicacionMapper::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public UbicacionResponseDTO obtener(Long id) {
        return UbicacionMapper.aRespuesta(buscar(id));
    }

    @Transactional
    public UbicacionResponseDTO crear(UbicacionRequestDTO dto) {
        Ubicacion ubicacion = UbicacionMapper.aEntidad(dto);
        return UbicacionMapper.aRespuesta(ubicacionRepository.save(ubicacion));
    }

    @Transactional
    public UbicacionResponseDTO actualizar(Long id, UbicacionRequestDTO dto) {
        Ubicacion ubicacion = buscar(id);
        UbicacionMapper.copiarDatos(ubicacion, dto);
        return UbicacionMapper.aRespuesta(ubicacionRepository.save(ubicacion));
    }

    /** RN-07: no se puede eliminar una ubicacion que tenga eventos asociados. */
    @Transactional
    public void eliminar(Long id) {
        Ubicacion ubicacion = buscar(id);
        if (eventoRepository.existsByUbicacion_Id(id)) {
            throw new ConflictException("No se puede eliminar una ubicacion con eventos asociados");
        }
        ubicacionRepository.delete(ubicacion);
    }

    Ubicacion buscar(Long id) {
        return ubicacionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("ubicacion", id));
    }
}
