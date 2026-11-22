package com.ues.poo.eventos.repository;

import com.ues.poo.eventos.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Consultas compartidas por la jerarquia Persona. No se expone como CRUD
 * porque Persona es una clase abstracta del modelo.
 */
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_IdAndIdNot(Long usuarioId, Long id);
}
