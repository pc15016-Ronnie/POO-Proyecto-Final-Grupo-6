package com.ues.poo.eventos.exception;

/** Datos invalidos que no cubren las anotaciones de validacion: se traduce en un 400. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
