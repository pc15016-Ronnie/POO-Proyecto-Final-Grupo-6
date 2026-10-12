package com.ues.poo.eventos.dto;

import com.ues.poo.eventos.model.EstadoRegistro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Elemento del listado de asistentes de un evento (CU-17). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenteEventoDTO {

    private Long registroId;
    private AsistenteResumenDTO asistente;
    private EstadoRegistro estado;
    private LocalDateTime fechaRegistro;
}
