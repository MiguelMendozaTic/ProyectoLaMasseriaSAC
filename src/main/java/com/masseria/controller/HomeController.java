package com.masseria.controller;

import com.masseria.entity.Contacto;
import com.masseria.entity.Usuario;
import com.masseria.service.ContactoService;
import com.masseria.service.ProductoService;
import com.masseria.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private ContactoService contactoService;  // Agregado

    // ========== PÁGINAS PRINCIPALES ==========
    
    @GetMapping("/")
    public String inicio(Model model) {
        // Productos por categorías
        model.addAttribute("desayunos", productoService.obtenerPorCategoria("Desayuno"));
        model.addAttribute("bebidasCalientes", productoService.obtenerPorCategoria("Bebida Caliente"));
        model.addAttribute("bebidasFrias", productoService.obtenerPorCategoria("Bebida Fría"));
        model.addAttribute("postres", productoService.obtenerPorCategoria("Postre"));
        
        // Productos destacados y novedades
        model.addAttribute("destacados", productoService.obtenerDestacados());
        model.addAttribute("novedades", productoService.obtenerTodos());
        
        // Categorías para el menú
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        
        // Página activa
        model.addAttribute("activePage", "inicio");
        
        return "index";
    }

    @GetMapping("/index")
    public String redireccionar() {
        return "redirect:/";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        model.addAttribute("activePage", "nosotros");
        return "nosotros";
    }

    @GetMapping("/contacto")
    public String contacto(Model model) {
        model.addAttribute("activePage", "contacto");
        return "contacto";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contacto";
    }

    @GetMapping("/reservaciones")
    public String reservaciones(Model model) {
        model.addAttribute("activePage", "reservaciones");
        return "reservacion";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("activePage", "pedidos");
        return "pedidos";
    }

    // ========== PROCESAMIENTO DE FORMULARIOS ==========
    
    @PostMapping("/reservaciones/guardar")
    public String guardarReservacion(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam Integer cantidadPersonas,
            @RequestParam(required = false) String notas,
            Model model) {
        
        // Aquí iría la lógica para guardar en BD
        System.out.println("Reservación guardada: " + nombre + " - " + fecha + " " + hora);
        
        model.addAttribute("mensaje", "¡Reservación realizada con éxito!");
        return "redirect:/reservaciones?exito";
    }

    @PostMapping("/pedidos/guardar")
    public String guardarPedido(
            @RequestParam String cliente,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam Double total,
            @RequestParam String metodoEntrega,
            @RequestParam(required = false) String direccion,
            @RequestParam String metodoPago,
            @RequestParam String detalles,
            Model model) {
        
        // Aquí iría la lógica para guardar en BD
        System.out.println("Pedido guardado: " + cliente + " - Total: " + total);
        
        model.addAttribute("mensaje", "¡Pedido realizado con éxito!");
        return "redirect:/pedidos?exito";
    }

    @PostMapping("/mensaje")
    public String enviarMensaje(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Crear objeto Contacto
        Contacto contacto = new Contacto();
        contacto.setNombre(nombre);
        contacto.setEmail(email);
        contacto.setAsunto(asunto);
        contacto.setMensaje(mensaje);
        
        // Si el usuario está logueado, asociarlo automáticamente
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            contacto.setUsuario(usuario);
            // Asegurar que el email coincida con el del usuario
            contacto.setEmail(usuario.getEmail());
        }
        
        // Guardar en la base de datos
        contactoService.guardar(contacto);
        
        System.out.println("Mensaje guardado de " + nombre + " (" + email + "): " + asunto);
        
        redirectAttributes.addFlashAttribute("mensaje", "Mensaje enviado con éxito. Te contactaremos pronto.");
        return "redirect:/contacto?enviado";
    }
}  