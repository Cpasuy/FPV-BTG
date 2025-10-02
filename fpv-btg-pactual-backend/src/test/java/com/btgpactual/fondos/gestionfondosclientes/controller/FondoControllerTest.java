package com.btgpactual.fondos.gestionfondosclientes.controller;

import com.btgpactual.fondos.gestionfondosclientes.dto.ApiResponse;
import com.btgpactual.fondos.gestionfondosclientes.dto.CancelacionRequest;
import com.btgpactual.fondos.gestionfondosclientes.dto.SuscripcionRequest;
import com.btgpactual.fondos.gestionfondosclientes.model.Cliente;
import com.btgpactual.fondos.gestionfondosclientes.model.Fondo;
import com.btgpactual.fondos.gestionfondosclientes.model.Transaccion;
import com.btgpactual.fondos.gestionfondosclientes.service.FondoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FondoController.class)
class FondoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FondoService fondoService;

    @Autowired
    private ObjectMapper objectMapper;

    private SuscripcionRequest suscripcionRequest;
    private CancelacionRequest cancelacionRequest;
    private Cliente cliente;
    private Fondo fondo;
    private Transaccion transaccion;

    @BeforeEach
    void setUp() {
        suscripcionRequest = new SuscripcionRequest();
        suscripcionRequest.setIdFondo("1");
        suscripcionRequest.setMonto(100000.0);
        suscripcionRequest.setPreferenciaNotificacion("EMAIL");
        suscripcionRequest.setContacto("test@example.com");

        cancelacionRequest = new CancelacionRequest();
        cancelacionRequest.setIdFondo("1");

        cliente = new Cliente();
        cliente.setId("CAROLINA-PASUY");
        cliente.setSaldo(500000.0);

        fondo = new Fondo("1", "FPV", "FPV_BTG_PACTUAL_RECAUDADORA", 75000.0);

        transaccion = new Transaccion("1", "SUSCRIPCION", "FPV_BTG_PACTUAL_RECAUDADORA", 100000.0, LocalDateTime.now());
    }

    @Test
    void suscribirseAFondoSuccess() throws Exception {
        when(fondoService.suscribirseAFondo(any(SuscripcionRequest.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/btg/fondos/suscribirse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suscripcionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Suscripción exitosa"))
                .andExpect(jsonPath("$.data.id").value("CAROLINA-PASUY"));
    }

    @Test
    void suscribirseAFondoError() throws Exception {
        when(fondoService.suscribirseAFondo(any(SuscripcionRequest.class)))
                .thenThrow(new IllegalArgumentException("Monto insuficiente"));

        mockMvc.perform(post("/api/btg/fondos/suscribirse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suscripcionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Monto insuficiente"));
    }

    @Test
    void cancelarSuscripcionSuccess() throws Exception {
        when(fondoService.cancelarSuscripcion(any(CancelacionRequest.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/btg/fondos/cancelar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelacionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cancelación exitosa"))
                .andExpect(jsonPath("$.data.id").value("CAROLINA-PASUY"));
    }

    @Test
    void cancelarSuscripcionError() throws Exception {
        when(fondoService.cancelarSuscripcion(any(CancelacionRequest.class)))
                .thenThrow(new RuntimeException("No está suscrito a este fondo"));

        mockMvc.perform(post("/api/btg/fondos/cancelar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelacionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No está suscrito a este fondo"));
    }

    @Test
    void obtenerHistorialSuccess() throws Exception {
        List<Transaccion> transacciones = Arrays.asList(transaccion);
        when(fondoService.obtenerHistorialTransacciones()).thenReturn(transacciones);

        mockMvc.perform(get("/api/btg/fondos/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Historial obtenido exitosamente"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].tipo").value("SUSCRIPCION"));
    }

    @Test
    void obtenerHistorialEmpty() throws Exception {
        when(fondoService.obtenerHistorialTransacciones()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/btg/fondos/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void obtenerHistorialError() throws Exception {
        when(fondoService.obtenerHistorialTransacciones())
                .thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(get("/api/btg/fondos/historial"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error al obtener el historial"));
    }

    @Test
    void obtenerFondosSuccess() throws Exception {
        List<Fondo> fondos = Arrays.asList(fondo);
        when(fondoService.obtenerFondos()).thenReturn(fondos);

        mockMvc.perform(get("/api/btg/fondos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fondos obtenidos exitosamente"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].nombre").value("FPV_BTG_PACTUAL_RECAUDADORA"));
    }
    @Test
    void obtenerFondosError() throws Exception {
        when(fondoService.obtenerFondos())
                .thenThrow(new RuntimeException("Error de base de datos"));

        mockMvc.perform(get("/api/btg/fondos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error al obtener los fondos"));
    }

    @Test
    void obtenerFondosEmpty() throws Exception {
        when(fondoService.obtenerFondos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/btg/fondos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }


}
