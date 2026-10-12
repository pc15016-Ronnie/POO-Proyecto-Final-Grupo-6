package com.ues.poo.eventos.exception;

import com.ues.poo.eventos.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

/** Manejo centralizado de errores: todas las respuestas usan el mismo formato. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(ResourceNotFoundException ex, HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflicto(ConflictException ex, HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> solicitudInvalida(BadRequestException ex, HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "Hay campos invalidos en la solicitud", request, detalles);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> restriccion(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> detalles = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "Hay valores invalidos en la solicitud", request, detalles);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoIlegible(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no se puede leer (revise el formato de fechas y los valores permitidos)",
                request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> parametroInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST,
                "El parametro '" + ex.getName() + "' tiene un valor invalido", request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT,
                "La operacion viola una restriccion de la base de datos (dato duplicado o referencia en uso)",
                request, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaNoEncontrada(NoResourceFoundException ex, HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND,
                "La ruta solicitada no existe: " + request.getRequestURI(), request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodoNoSoportado(HttpRequestMethodNotSupportedException ex,
                                                          HttpServletRequest request) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED,
                "El metodo HTTP " + ex.getMethod() + " no esta permitido en esta ruta", request, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> tipoNoSoportado(HttpMediaTypeNotSupportedException ex,
                                                        HttpServletRequest request) {
        return construir(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El tipo de contenido no es soportado; use application/json", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> errorInesperado(Exception ex, HttpServletRequest request) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrio un error inesperado al procesar la solicitud", request, null);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus estado, String mensaje,
                                                    HttpServletRequest request, List<String> detalles) {
        ErrorResponse respuesta = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(estado.value())
                .error(estado.getReasonPhrase())
                .message(mensaje)
                .path(request.getRequestURI())
                .details(detalles)
                .build();
        return ResponseEntity.status(estado).body(respuesta);
    }
}
