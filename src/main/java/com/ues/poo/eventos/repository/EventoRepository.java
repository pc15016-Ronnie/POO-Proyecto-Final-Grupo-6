package com.ues.poo.eventos.repository;

import com.ues.poo.eventos.model.Evento;
import com.ues.poo.eventos.model.EstadoEvento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {

    /** CU-13: eventos publicados con fecha futura. */
    List<Evento> findByEstadoAndFechaInicioAfterOrderByFechaInicioAsc(EstadoEvento estado, LocalDateTime momento);

    /** RN-07: no se puede eliminar una ubicacion con eventos asociados. */
    boolean existsByUbicacion_Id(Long ubicacionId);

    List<Evento> findByUbicacion_IdOrderByFechaInicioAsc(Long ubicacionId);

    /**
     * Bloquea la fila del evento mientras se valida el aforo, para evitar
     * condiciones de carrera con inscripciones simultaneas (nota RN-02 del ERD).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Evento e where e.id = :id")
    Optional<Evento> buscarParaRegistro(@Param("id") Long id);
}
