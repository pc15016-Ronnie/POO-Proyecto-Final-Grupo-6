package com.ues.poo.eventos.exception;

/** Recurso inexistente: se traduce en un 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException de(String recurso, Long id) {
        return new ResourceNotFoundException("El " + recurso + " indicado no existe (id " + id + ")");
    }
}
