package com.ues.poo.eventos.service;

import com.ues.poo.eventos.controller.UbicacionController;
import com.ues.poo.eventos.dto.UbicacionRequestDTO;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.GlobalExceptionHandler;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Contrato HTTP de validacion y errores centralizados. */
class ApiErrorHandlingTest {

    private UbicacionService ubicacionService;
    private MockMvc mockMvc;

    @BeforeEach
    void prepararMvc() {
        ubicacionService = mock(UbicacionService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UbicacionController(ubicacionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("HTTP-400: devuelve detalles de Bean Validation")
    void devuelveValidation400() throws Exception {
        mockMvc.perform(post("/api/ubicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"","capacidad":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Hay campos invalidos en la solicitud"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("HTTP-404: devuelve el formato uniforme para recurso inexistente")
    void devuelveNotFound404() throws Exception {
        when(ubicacionService.obtener(99L))
                .thenThrow(ResourceNotFoundException.de("ubicacion", 99L));

        mockMvc.perform(get("/api/ubicaciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/ubicaciones/99"))
                .andExpect(jsonPath("$.message").value("El ubicacion indicado no existe (id 99)"));
    }

    @Test
    @DisplayName("HTTP-409: devuelve conflicto de regla de negocio")
    void devuelveConflict409() throws Exception {
        when(ubicacionService.crear(any(UbicacionRequestDTO.class)))
                .thenThrow(new ConflictException("La ubicacion esta en uso"));

        mockMvc.perform(post("/api/ubicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Auditorio","capacidad":10}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("La ubicacion esta en uso"));
    }

    @Test
    @DisplayName("HTTP-500: oculta la causa interna del error inesperado")
    void devuelveInternal500() throws Exception {
        when(ubicacionService.obtener(1L)).thenThrow(new IllegalStateException("detalle interno"));

        mockMvc.perform(get("/api/ubicaciones/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message")
                        .value("Ocurrio un error inesperado al procesar la solicitud"));
    }

    @Test
    @DisplayName("HTTP-400: devuelve un mensaje controlado para JSON ilegible")
    void devuelveJsonIlegible400() throws Exception {
        mockMvc.perform(post("/api/ubicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "El cuerpo de la solicitud no se puede leer (revise el formato de fechas y los valores permitidos)"));
    }

    @Test
    @DisplayName("HTTP-400: rechaza un identificador no numerico")
    void devuelveParametroInvalido400() throws Exception {
        mockMvc.perform(get("/api/ubicaciones/no-es-numero"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El parametro 'id' tiene un valor invalido"));
    }

    @Test
    @DisplayName("HTTP-405: documenta y uniforma metodo no permitido")
    void devuelveMethodNotAllowed405() throws Exception {
        mockMvc.perform(patch("/api/ubicaciones/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    @DisplayName("HTTP-415: documenta y uniforma tipo de contenido no soportado")
    void devuelveMediaType415() throws Exception {
        mockMvc.perform(post("/api/ubicaciones")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("texto"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415));
    }
}
