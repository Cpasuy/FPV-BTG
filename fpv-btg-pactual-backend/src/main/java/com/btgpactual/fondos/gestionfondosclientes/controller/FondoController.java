package com.btgpactual.fondos.gestionfondosclientes.controller;

import com.btgpactual.fondos.gestionfondosclientes.dto.ApiResponse;
import com.btgpactual.fondos.gestionfondosclientes.dto.CancelacionRequest;
import com.btgpactual.fondos.gestionfondosclientes.dto.SuscripcionRequest;
import com.btgpactual.fondos.gestionfondosclientes.model.Cliente;
import com.btgpactual.fondos.gestionfondosclientes.model.Fondo;
import com.btgpactual.fondos.gestionfondosclientes.model.Transaccion;
import com.btgpactual.fondos.gestionfondosclientes.service.FondoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/btg/fondos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Gestión de Fondos", description = "API para gestión de fondos de inversión")
public class FondoController {
    
    private final FondoService fondoService;
    
    /**
     * Suscribe al cliente a un fondo de inversión
     * @param request Datos de la suscripción (fondo, monto, notificación)
     * @return Cliente actualizado con la nueva suscripción
     */
    @PostMapping("/suscribirse")
    @Operation(summary = "Suscribirse a un fondo", description = "Permite al cliente suscribirse a un fondo ")
    public ResponseEntity<ApiResponse<Cliente>> suscribirseAFondo(@Valid @RequestBody SuscripcionRequest request) {

        try {
            Cliente cliente = fondoService.suscribirseAFondo(request);
            return ResponseEntity.ok(ApiResponse.success("Suscripción exitosa", cliente));
        } catch (Exception e) {
            log.error("Error en suscripción: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cancela la suscripción del cliente a un fondo
     * @param request ID del fondo a cancelar
     * @return Cliente actualizado sin la suscripción cancelada
     */
    @PostMapping("/cancelar")
    @Operation(summary = "Cancelar suscripción", description = "Permite al cliente cancelar su suscripción a un fondo")
    public ResponseEntity<ApiResponse<Cliente>> cancelarSuscripcion(@Valid @RequestBody CancelacionRequest request) {

        try {
            Cliente cliente = fondoService.cancelarSuscripcion(request);
            return ResponseEntity.ok(ApiResponse.success("Cancelación exitosa", cliente));
        } catch (Exception e) {
            log.error("Error en cancelación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Obtiene el historial de transacciones del cliente
     * @return Lista de todas las transacciones (suscripciones y cancelaciones)
     */
    @GetMapping("/historial")
    @Operation(summary = "Obtener historial", description = "Obtiene el historial de transacciones del cliente")
    public ResponseEntity<ApiResponse<List<Transaccion>>> obtenerHistorial() {

        try {
            List<Transaccion> historial = fondoService.obtenerHistorialTransacciones();
            return ResponseEntity.ok(ApiResponse.success("Historial obtenido exitosamente", historial));
        } catch (Exception e) {
            log.error("Error al obtener historial: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al obtener el historial"));
        }
    }
    
    /**
     * Obtiene todos los fondos disponibles
     * @return Lista de todos los fondos de inversión
     */
    @GetMapping("")
    @Operation(summary = "Obtener fondos disponibles", description = "Obtiene la lista de todos los fondos de inversión disponibles")
    public ResponseEntity<ApiResponse<List<Fondo>>> obtenerFondos() {
        try {
            List<Fondo> fondos = fondoService.obtenerFondos();
            return ResponseEntity.ok(ApiResponse.success("Fondos obtenidos exitosamente", fondos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al obtener los fondos"));
        }
    }
    
    
}
