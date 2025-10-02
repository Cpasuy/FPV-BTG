package com.btgpactual.fondos.gestionfondosclientes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    @Id
    private String idTransaccion;
    private String tipo;
    private String fondo;
    private Double monto;
    private LocalDateTime fecha;
}
