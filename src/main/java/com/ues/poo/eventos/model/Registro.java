package com.ues.poo.eventos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Inscripcion de un asistente a un evento.
 * Es la entidad intermedia que resuelve la relacion N:M entre EVENTO y ASISTENTE
 * y donde se valida el aforo (RN-02, RN-03, RN-04 y RN-11).
 */
@Entity
@Table(name = "registro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asistente_id", nullable = false)
    private Asistente asistente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRegistro estado;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @PrePersist
    void alCrear() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoRegistro.ACTIVO;
        }
    }

    public boolean estaActivo() {
        return this.estado == EstadoRegistro.ACTIVO;
    }

    /** CU-15 y RN-11: cancela la inscripcion y marca la fecha de cancelacion. */
    public void cancelar() {
        this.estado = EstadoRegistro.CANCELADO;
        this.fechaCancelacion = LocalDateTime.now();
    }
}
