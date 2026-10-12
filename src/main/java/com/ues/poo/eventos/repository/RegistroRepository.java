package com.ues.poo.eventos.repository;

import com.ues.poo.eventos.model.EstadoRegistro;
import com.ues.poo.eventos.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    /** RN-02: cupo disponible = capacidad maxima - registros ACTIVOS. */
    long countByEvento_IdAndEstado(Long eventoId, EstadoRegistro estado);

    /** RN-04: un asistente no puede tener dos registros activos en el mismo evento. */
    boolean existsByEvento_IdAndAsistente_IdAndEstado(Long eventoId, Long asistenteId, EstadoRegistro estado);

    long countByAsistente_IdAndEstado(Long asistenteId, EstadoRegistro estado);

    /** CU-17: asistentes inscritos (activos) en un evento. */
    List<Registro> findByEvento_IdAndEstadoOrderByFechaRegistroAsc(Long eventoId, EstadoRegistro estado);

    List<Registro> findByEvento_Id(Long eventoId);

    void deleteByEvento_Id(Long eventoId);

    void deleteByAsistente_Id(Long asistenteId);
}
