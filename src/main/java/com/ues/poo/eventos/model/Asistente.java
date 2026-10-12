package com.ues.poo.eventos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Perfil de la persona que se inscribe a eventos (CU-09 a CU-12, CU-17).
 * Hereda nombre, apellido, email y telefono de Persona.
 */
@Entity
@Table(name = "asistente")
@DiscriminatorValue("ASISTENTE")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class Asistente extends Persona {
}
