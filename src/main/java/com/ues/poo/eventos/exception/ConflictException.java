package com.ues.poo.eventos.exception;

/** Conflicto con una regla de negocio o un dato duplicado: se traduce en un 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
