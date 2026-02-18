package com.masseria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @GetMapping("/desayunos")
    public String desayunos() {
        return "menu/desayunos"; // Busca en templates/menu/desayunos.html
    }

    @GetMapping("/bebidas-calientes")
    public String bebidasCalientes() {
        return "menu/bebidas-calientes"; // Busca en templates/menu/bebidas-calientes.html
    }

    @GetMapping("/bebidas-frias")
    public String bebidasFrias() {
        return "menu/bebidas-frias"; // Busca en templates/menu/bebidas-frias.html
    }

    @GetMapping("/postres")
    public String postres() {
        return "menu/postres"; // Busca en templates/menu/postres.html
    }
}