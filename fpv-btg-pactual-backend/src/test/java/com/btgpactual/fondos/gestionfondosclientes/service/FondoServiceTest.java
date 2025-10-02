package com.btgpactual.fondos.gestionfondosclientes.service;

import com.btgpactual.fondos.gestionfondosclientes.dto.CancelacionRequest;
import com.btgpactual.fondos.gestionfondosclientes.dto.SuscripcionRequest;
import com.btgpactual.fondos.gestionfondosclientes.exception.FondoNotFoundException;
import com.btgpactual.fondos.gestionfondosclientes.exception.InsufficientFundsException;
import com.btgpactual.fondos.gestionfondosclientes.exception.SuscripcionNotFoundException;
import com.btgpactual.fondos.gestionfondosclientes.model.Cliente;
import com.btgpactual.fondos.gestionfondosclientes.model.Fondo;
import com.btgpactual.fondos.gestionfondosclientes.model.FondoActivo;
import com.btgpactual.fondos.gestionfondosclientes.model.Transaccion;
import com.btgpactual.fondos.gestionfondosclientes.repository.ClienteRepository;
import com.btgpactual.fondos.gestionfondosclientes.repository.FondoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FondoServiceTest {
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private FondoRepository fondoRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private FondoService fondoService;
    
    private Fondo fondo;
    private Cliente cliente;
    private SuscripcionRequest suscripcionRequest;
    
    @BeforeEach
    void setUp() {
        fondo = new Fondo("1", "FPV" , "FPV_BTG_PACTUAL_RECAUDADORA", 75000.0);
        cliente = new Cliente();
        cliente.setId("CAROLINA-PASUY");
        cliente.setSaldo(500000.0);
        
        suscripcionRequest = new SuscripcionRequest();
        suscripcionRequest.setIdFondo("1");
        suscripcionRequest.setMonto(100000.0);
        suscripcionRequest.setPreferenciaNotificacion("EMAIL");
        suscripcionRequest.setContacto("carolinapasuy@hotmail..com");
    }
    
    @Test
    void suscribirseAFondoSuccess() {
        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("CAROLINA-PASUY")).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente result = fondoService.suscribirseAFondo(suscripcionRequest);

        assertNotNull(result);
        assertEquals(400000.0, result.getSaldo());
        assertEquals(1, result.getFondosActivos().size());
        verify(notificationService).enviarNotificacionSuscripcion(any(), any(), any());
    }
    @Test
    void suscribirseAFondoMontoInferiorAlMinimo() {
        suscripcionRequest.setMonto(10000.0);
        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("CAROLINA-PASUY")).thenReturn(Optional.of(cliente));

        assertThrows(IllegalArgumentException.class, () -> {
            fondoService.suscribirseAFondo(suscripcionRequest);
        });
    }
    
    @Test
    void suscribirseAFondoNoEncontrado() {
        when(fondoRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(FondoNotFoundException.class, () -> {
            fondoService.suscribirseAFondo(suscripcionRequest);
        });
    }
    
    @Test
    void suscribirseAFondoFondosInsuficientes() {
        suscripcionRequest.setMonto(800000.0);
        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.findById("CAROLINA-PASUY")).thenReturn(Optional.of(cliente));

        assertThrows(InsufficientFundsException.class, () -> {
            fondoService.suscribirseAFondo(suscripcionRequest);
        });
    }
    

    
    @Test
    void obtenerHistorialTransaccionesSuccess() {
        Cliente cliente = new Cliente();
        cliente.setId("CAROLINA-PASUY");
        cliente.setHistorialTransacciones(Arrays.asList(
            new Transaccion("1", "SUSCRIPCION", "Fondo1", 100000.0, LocalDateTime.now()),
            new Transaccion("2", "CANCELACION", "Fondo1", 100000.0, LocalDateTime.now())
        ));
        
        when(clienteRepository.findById("CAROLINA-PASUY")).thenReturn(Optional.of(cliente));

        List<Transaccion> historial = fondoService.obtenerHistorialTransacciones();

        assertEquals(2, historial.size());
        assertEquals("SUSCRIPCION", historial.get(0).getTipo());
        assertEquals("CANCELACION", historial.get(1).getTipo());
    }

    @Test
    void obtenerFondosSuccess() {
        Fondo fondo = new Fondo();
        fondo.setId("1");
        fondo.setCategoria("FPV");
        fondo.setNombre("FPV_BTG_PACTUAL_RECAUDADORA");
        fondo.setMontoMinimo(75000.0);

        when(fondoRepository.findAll()).thenReturn(Arrays.asList(fondo));
        List<Fondo> fondos = fondoService.obtenerFondos();


        assertEquals(1, fondos.size());
        assertEquals("1", fondos.get(0).getId());
        assertEquals("FPV_BTG_PACTUAL_RECAUDADORA", fondos.get(0).getNombre());
    }

    @Test
    void obtenerFondosEmpty() {
        when(fondoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Fondo> fondos = fondoService.obtenerFondos();

        assertTrue(fondos.isEmpty(), "La lista de fondos debería estar vacía");
    }

    @Test
    void cancelarSuscripcionSuccess() {
        FondoActivo fondoActivo = new FondoActivo("1", "FPV_BTG_PACTUAL_RECAUDADORA", 100000.0, LocalDateTime.now());
        cliente.getFondosActivos().add(fondoActivo);
        
        CancelacionRequest cancelacionRequest = new CancelacionRequest();
        cancelacionRequest.setIdFondo("1");
        
        when(clienteRepository.findById("CAROLINA-PASUY")).thenReturn(Optional.of(cliente));
        when(fondoRepository.findById("1")).thenReturn(Optional.of(fondo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente result = fondoService.cancelarSuscripcion(cancelacionRequest);

        assertNotNull(result);
        assertEquals(600000.0, result.getSaldo());
        assertTrue(result.getFondosActivos().isEmpty());
        verify(notificationService).enviarNotificacionCancelacion(any(), any(), any());
    }

    @Test
    void cancelarSuscripcionNoSuscrito() {
        CancelacionRequest cancelacionRequest = new CancelacionRequest();
        cancelacionRequest.setIdFondo("1");
        
        when(clienteRepository.findById("CAROLINA-PASUY")).thenReturn(Optional.of(cliente));

        assertThrows(SuscripcionNotFoundException.class, () -> {
            fondoService.cancelarSuscripcion(cancelacionRequest);
        });
    }

    @Test
    void cancelarSuscripcionFondoNoEncontrado() {
        FondoActivo fondoActivo = new FondoActivo("1", "FPV_BTG_PACTUAL_RECAUDADORA", 100000.0, LocalDateTime.now());
        cliente.getFondosActivos().add(fondoActivo);
        
        CancelacionRequest cancelacionRequest = new CancelacionRequest();
        cancelacionRequest.setIdFondo("1");
        
        when(clienteRepository.findById("CAROLINA-PASUY")).thenReturn(Optional.of(cliente));
        when(fondoRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(FondoNotFoundException.class, () -> {
            fondoService.cancelarSuscripcion(cancelacionRequest);
        });
    }

    @Test
    void obtenerHistorialTransaccionesClienteSinTransacciones() {
        Cliente clienteSinTransacciones = new Cliente();
        clienteSinTransacciones.setId("CAROLINA-PASUY");
        clienteSinTransacciones.setHistorialTransacciones(Collections.emptyList());
        
        when(clienteRepository.findById("CAROLINA-PASUY")).thenReturn(Optional.of(clienteSinTransacciones));

        List<Transaccion> historial = fondoService.obtenerHistorialTransacciones();

        assertNotNull(historial);
        assertTrue(historial.isEmpty(), "El historial debería estar vacío");
    }

}
