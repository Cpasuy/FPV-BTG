package com.btgpactual.fondos.gestionfondosclientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelacionRequest {
    @NotBlank(message = "El ID del fondo es obligatorio")
    private String idFondo;
}
