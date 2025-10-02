package com.btgpactual.fondos.gestionfondosclientes.service;

import com.btgpactual.fondos.gestionfondosclientes.model.Fondo;
import com.btgpactual.fondos.gestionfondosclientes.repository.FondoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Servicio para inicializar datos básicos del sistema
 * Se ejecuta al iniciar la aplicación y crea los fondos disponibles
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {
    
    private final FondoRepository fondoRepository;
    
    /**
     * Se ejecuta al iniciar la aplicación
     * Inicializa los fondos si la base de datos está vacía
     * @param args Argumentos de la aplicación
     */
    @Override
    public void run(String... args) throws Exception {
        if (fondoRepository.count() == 0) {
            inicializarFondos();
            log.info("Datos inicializados correctamente");
        }
    }
    
    /**
     * Crea los fondos iniciales del sistema
     * Incluye FPV y FIC con sus montos mínimos
     */
    private void inicializarFondos() {
        List<Fondo> fondos = Arrays.asList(
            new Fondo("1","FPV","FPV_BTG_PACTUAL_RECAUDADORA", 75000.0),
            new Fondo("2","FPV", "FPV_BTG_PACTUAL_ECOPETROL", 125000.0),
            new Fondo("3","FIC", "DEUDAPRIVADA", 50000.0),
            new Fondo("4","FIC", "FDO-ACCIONES", 250000.0),
            new Fondo("5","FPV", "FPV_BTG_PACTUAL_DINAMICA", 100000.0)
        );
        
        fondoRepository.saveAll(fondos);
    }
}
