package com.ues.poo.eventos.service;

import com.ues.poo.eventos.dto.UbicacionRequestDTO;
import com.ues.poo.eventos.exception.ConflictException;
import com.ues.poo.eventos.exception.ResourceNotFoundException;
import com.ues.poo.eventos.model.Ubicacion;
import com.ues.poo.eventos.repository.EventoRepository;
import com.ues.poo.eventos.repository.UbicacionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del catalogo de ubicaciones (RN-07 y CU-05 a CU-08). */
@ExtendWith(MockitoExtension.class)
class UbicacionServiceTest {

    @Mock
    private UbicacionRepository ubicacionRepository;
    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private UbicacionService ubicacionService;

    private UbicacionRequestDTO dto() {
        return new UbicacionRequestDTO("Auditorio Central", "Ciudad Universitaria", "Santa Ana",
                250, 13.9945, -89.5597);
    }

    @Test
    @DisplayName("CU-05: crea la ubicacion con los datos enviados")
    void creaUbicacion() {
        when(ubicacionRepository.save(any(Ubicacion.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = ubicacionService.crear(dto());

        assertThat(respuesta.getNombre()).isEqualTo("Auditorio Central");
        assertThat(respuesta.getCapacidad()).isEqualTo(250);
        assertThat(respuesta.getCiudad()).isEqualTo("Santa Ana");
    }

    @Test
    @DisplayName("CU-06: lista las ubicaciones ordenadas por nombre")
    void listaUbicaciones() {
        when(ubicacionRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(Ubicacion.builder().id(1L).nombre("Auditorio Central").capacidad(250).build()));

        var respuesta = ubicacionService.listar();

        assertThat(respuesta).hasSize(1);
        assertThat(respuesta.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("CU-06: responde 404 cuando la ubicacion no existe")
    void ubicacionInexistente() {
        when(ubicacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ubicacionService.obtener(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("CU-07: actualiza los datos de una ubicacion existente")
    void actualizaUbicacion() {
        Ubicacion existente = Ubicacion.builder().id(1L).nombre("Antiguo").capacidad(10).build();
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(ubicacionRepository.save(any(Ubicacion.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = ubicacionService.actualizar(1L, dto());

        assertThat(respuesta.getNombre()).isEqualTo("Auditorio Central");
        assertThat(respuesta.getCapacidad()).isEqualTo(250);
    }

    @Test
    @DisplayName("RN-07: no permite eliminar una ubicacion con eventos asociados")
    void rechazaEliminarConEventos() {
        Ubicacion ubicacion = Ubicacion.builder().id(1L).nombre("Auditorio Central").capacidad(250).build();
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacion));
        when(eventoRepository.existsByUbicacion_Id(1L)).thenReturn(true);

        assertThatThrownBy(() -> ubicacionService.eliminar(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("eventos asociados");

        verify(ubicacionRepository, never()).delete(any(Ubicacion.class));
    }

    @Test
    @DisplayName("CU-08: elimina la ubicacion cuando no tiene eventos")
    void eliminaUbicacion() {
        Ubicacion ubicacion = Ubicacion.builder().id(1L).nombre("Auditorio Central").capacidad(250).build();
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacion));
        when(eventoRepository.existsByUbicacion_Id(1L)).thenReturn(false);

        ubicacionService.eliminar(1L);

        verify(ubicacionRepository).delete(ubicacion);
    }
}
