package com.btgpactual.fondos.gestionfondosclientes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clientes")
public class Cliente {
    @Id
    private String id;
    private Double saldo = 500000.0;
    private List<FondoActivo> fondosActivos = new ArrayList<>();
    private String preferenciaNotificacion;
    private String contacto;
    private List<Transaccion> historialTransacciones = new ArrayList<>();
}
