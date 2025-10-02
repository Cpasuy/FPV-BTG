package com.btgpactual.fondos.gestionfondosclientes.service;

import com.btgpactual.fondos.gestionfondosclientes.model.Cliente;
import com.btgpactual.fondos.gestionfondosclientes.model.Fondo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final JavaMailSender mailSender;
    
    /**
     * Envía notificación de suscripción exitosa
     * @param cliente Cliente que se suscribió
     * @param fondo Fondo al que se suscribió
     * @param monto Monto de la suscripción
     */
    public void enviarNotificacionSuscripcion(Cliente cliente, Fondo fondo, Double monto) {
        String mensaje = String.format(
            "Estimado cliente,\n\n" +
            "Se ha realizado exitosamente su suscripción al fondo %s por un monto de $%,.2f COP.\n\n" +
            "Gracias por confiar en BTG Pactual.\n\n" +
            "Saludos cordiales,\n" +
            "Equipo BTG Pactual",
            fondo.getNombre(), monto
        );
        
        if ("EMAIL".equals(cliente.getPreferenciaNotificacion())) {
            enviarEmail(cliente.getContacto(), "Confirmación de Suscripción - BTG Pactual", mensaje);
        } else if ("SMS".equals(cliente.getPreferenciaNotificacion())) {
            enviarSMS(cliente.getContacto(), mensaje);
        }
    }
    
    /**
     * Envía notificación de cancelación exitosa
     * @param cliente Cliente que canceló
     * @param fondo Fondo que canceló
     * @param monto Monto devuelto
     */
    public void enviarNotificacionCancelacion(Cliente cliente, Fondo fondo, Double monto) {
        String mensaje = String.format(
            "Estimado cliente,\n\n" +
            "Se ha realizado exitosamente la cancelación de su suscripción al fondo %s. " +
            "El monto de $%,.2f COP ha sido devuelto a su cuenta.\n\n" +
            "Gracias por confiar en BTG Pactual.\n\n" +
            "Saludos cordiales,\n" +
            "Equipo BTG Pactual",
            fondo.getNombre(), monto
        );
        
        if ("EMAIL".equals(cliente.getPreferenciaNotificacion())) {
            enviarEmail(cliente.getContacto(), "Confirmación de Cancelación - BTG Pactual", mensaje);
        } else if ("SMS".equals(cliente.getPreferenciaNotificacion())) {
            enviarSMS(cliente.getContacto(), mensaje);
        }
    }
    
    private void enviarEmail(String email, String asunto, String mensaje) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(asunto);
            message.setText(mensaje);
            message.setFrom("noreply@btgpactual.com");
            
            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", email, e.getMessage());
        }
    }
    
    private void enviarSMS(String telefono, String mensaje) {

        log.info("SMS SIMULADO:");
        log.info("Para: {}", telefono);
        log.info("Mensaje: {}", mensaje);
        log.info("SMS simulado enviado exitosamente a {}", telefono);

    }
}
