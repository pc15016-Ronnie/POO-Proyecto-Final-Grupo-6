package com.ues.poo.eventos.repository;

import com.ues.poo.eventos.model.Asistente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AsistenteRepository extends JpaRepository<Asistente, Long> {

    Optional<Asistente> findByEmailIgnoreCase(String email);

    /** RN-09: el correo electronico debe ser unico. */
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
