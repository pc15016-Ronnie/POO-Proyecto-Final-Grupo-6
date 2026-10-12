package com.ues.poo.eventos.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Perfil de negocio del actor Administrador (CU-01 a CU-12).
 * No expone endpoints propios en la Entrega #2: existe para que la base de
 * datos coincida con el modelo alineado.
 */
@Entity
@Table(name = "administrador")
@DiscriminatorValue("ADMINISTRADOR")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Persona {

    @Column(length = 80)
    private String departamento;
}
