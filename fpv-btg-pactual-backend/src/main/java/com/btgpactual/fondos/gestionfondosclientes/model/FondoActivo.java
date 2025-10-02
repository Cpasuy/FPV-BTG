package com.btgpactual.fondos.gestionfondosclientes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FondoActivo {
    @Id
    private String idFondo;
    private String nombre;
    private Double monto;
    private LocalDateTime fechaSuscripcion;
}
