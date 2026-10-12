package com.ues.poo.eventos.repository;

import com.ues.poo.eventos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio de credenciales. En la Entrega #2 no expone endpoints:
 * queda listo para el inicio de sesion que la Entrega #3 pueda requerir.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}
