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
import com.btgpactual.fondos.gestionfondosclientes.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FondoService {
    
    private final ClienteRepository clienteRepository;
    private final FondoRepository fondoRepository;
    private final NotificationService notificationService;

    
    /**
     * Suscribe al cliente a un fondo de inversión
     * Valida monto mínimo, saldo suficiente y envía notificación
     * @param request Datos de la suscripción
     * @return Cliente actualizado con la nueva suscripción
     * @throws FondoNotFoundException Si el fondo no existe
     * @throws InsufficientFundsException Si no hay saldo suficiente
     */
    @Transactional
    public Cliente suscribirseAFondo(SuscripcionRequest request) {
        Cliente cliente = obtenerCliente();
        Fondo fondo = obtenerFondo(request.getIdFondo());

        validarSuscripcion(cliente, fondo, request);

        aplicarSuscripcion(cliente, fondo, request);
        registrarTransaccion(cliente, fondo, request);

        Cliente clienteGuardado = clienteRepository.save(cliente);
        notificarSuscripcion(clienteGuardado, fondo, request);

        log.info("Suscripción exitosa al fondo: {} por monto: {}", fondo.getNombre(), request.getMonto());
        return clienteGuardado;
    }
    
    /**
     * Cancela la suscripción del cliente a un fondo
     * Devuelve el monto al cliente y envía notificación
     * @param request ID del fondo a cancelar
     * @return Cliente actualizado sin la suscripción
     * @throws SuscripcionNotFoundException Si no está suscrito al fondo
     */
    @Transactional
    public Cliente cancelarSuscripcion(CancelacionRequest request) {
        log.info("Iniciando cancelación del fondo: {}", request.getIdFondo());
        
        Cliente cliente = clienteRepository.findById(Constants.CLIENTE_ID)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        FondoActivo fondoActivo = cliente.getFondosActivos().stream()
            .filter(fa -> fa.getIdFondo().equals(request.getIdFondo()))
            .findFirst()
            .orElseThrow(() -> new SuscripcionNotFoundException("No está suscrito a este fondo"));

        Fondo fondo = fondoRepository.findById(request.getIdFondo())
            .orElseThrow(() -> new FondoNotFoundException("Fondo no encontrado"));

        cliente.setSaldo(cliente.getSaldo() + fondoActivo.getMonto());
        cliente.getFondosActivos().remove(fondoActivo);

        Transaccion transaccion = new Transaccion(
            UUID.randomUUID().toString(),
                Constants.TIPO_TRANSACCION_CANCELACION,
            fondo.getNombre(),
            fondoActivo.getMonto(),
            LocalDateTime.now()
        );
        cliente.getHistorialTransacciones().add(transaccion);

        Cliente clienteGuardado = clienteRepository.save(cliente);

        notificationService.enviarNotificacionCancelacion(clienteGuardado, fondo, fondoActivo.getMonto());
        
        log.info("Cancelación exitosa del fondo: {} por monto: {}", fondo.getNombre(), fondoActivo.getMonto());
        return clienteGuardado;
    }
    
    /**
     * Obtiene el historial completo de transacciones del cliente
     * @return Lista de todas las transacciones realizadas
     */
    public List<Transaccion> obtenerHistorialTransacciones() {
        Cliente cliente = clienteRepository.findById(Constants.CLIENTE_ID)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        return cliente.getHistorialTransacciones();
    }
    
    /**
     * Obtiene todos los fondos disponibles
     * @return Lista de todos los fondos de inversión
     */
    public List<Fondo> obtenerFondos() {
        List<Fondo> fondos = fondoRepository.findAll();
        return fondos;
    }
    
    
    
    /**
     * Crea un cliente inicial con saldo de $500,000 COP
     * @return Cliente nuevo con saldo inicial
     */
    private Cliente crearClienteInicial() {
        Cliente cliente = new Cliente();
        cliente.setId(Constants.CLIENTE_ID);
        cliente.setSaldo(500000.0);
        return cliente;
    }

    private Cliente obtenerCliente() {
        return clienteRepository.findById(Constants.CLIENTE_ID)
                .orElse(crearClienteInicial());
    }
    private Fondo obtenerFondo(String idFondo) {
        return fondoRepository.findById(idFondo)
                .orElseThrow(() -> new FondoNotFoundException("El fondo " + idFondo + " no existe"));
    }

    private void validarSuscripcion(Cliente cliente, Fondo fondo, SuscripcionRequest request) {
        if (request.getMonto() < fondo.getMontoMinimo()) {
            throw new IllegalArgumentException(String.format(
                    Constants.MSG_MONTO_MINIMO,
                    fondo.getNombre(), fondo.getMontoMinimo()));
        }

        if (cliente.getSaldo() < request.getMonto()) {
            throw new InsufficientFundsException(String.format(
                    Constants.MSG_SALDO_INSUFICIENTE, fondo.getNombre()));
        }

        boolean yaSuscrito = cliente.getFondosActivos().stream()
                .anyMatch(fa -> fa.getIdFondo().equals(fondo.getId()));
        if (yaSuscrito) {
            throw new IllegalArgumentException(Constants.MSG_SUSCRIPCION_ACTIVA);
        }
    }

    private void aplicarSuscripcion(Cliente cliente, Fondo fondo, SuscripcionRequest request) {
        cliente.setSaldo(cliente.getSaldo() - request.getMonto());
        cliente.setPreferenciaNotificacion(request.getPreferenciaNotificacion());
        cliente.setContacto(request.getContacto());

        FondoActivo fondoActivo = new FondoActivo(
                fondo.getId(),
                fondo.getNombre(),
                request.getMonto(),
                LocalDateTime.now()
        );
        cliente.getFondosActivos().add(fondoActivo);
    }

    private void registrarTransaccion(Cliente cliente, Fondo fondo, SuscripcionRequest request) {
        Transaccion transaccion = new Transaccion(
                UUID.randomUUID().toString(),
                Constants.TIPO_TRANSACCION_SUSCRIPCION,
                fondo.getNombre(),
                request.getMonto(),
                LocalDateTime.now()
        );
        cliente.getHistorialTransacciones().add(transaccion);
    }
    private void notificarSuscripcion(Cliente cliente, Fondo fondo, SuscripcionRequest request) {
        notificationService.enviarNotificacionSuscripcion(cliente, fondo, request.getMonto());
    }


}
