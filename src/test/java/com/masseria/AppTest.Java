package com.masseria;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProyectoMasseriaApplicationTests {

    @Test
    void contextLoads() {
        // Esta prueba verifica que el contexto de Spring Boot se carga correctamente
        System.out.println("Contexto de Spring Boot cargado correctamente");
    }
    
    @Test
    void testSimple() {
        // Una prueba muy simple que siempre pasa
        assert true;
        System.out.println("Prueba simple ejecutada");
    }
}