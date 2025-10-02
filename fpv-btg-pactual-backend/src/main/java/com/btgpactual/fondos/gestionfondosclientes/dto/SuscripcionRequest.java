package com.btgpactual.fondos.gestionfondosclientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionRequest {
    @NotBlank(message = "El ID del fondo es obligatorio")
    private String idFondo;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double monto;
    
    @NotBlank(message = "La preferencia de notificación es obligatoria")
    private String preferenciaNotificacion; // "EMAIL" o "SMS"
    
    @NotBlank(message = "El contacto es obligatorio")
    private String contacto; // Email o número de teléfono
}
