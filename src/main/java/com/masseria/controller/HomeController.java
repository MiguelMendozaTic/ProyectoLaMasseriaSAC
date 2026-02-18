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
    private ContactoService contactoService;

    // ========== PÁGINAS PÚBLICAS (NO REQUIEREN LOGIN) ==========
    
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

    // ========== PÁGINAS PROTEGIDAS (REQUIEREN LOGIN) ==========
    
    @GetMapping("/reservaciones")
    public String reservaciones(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // PASO 1: Verificar si hay usuario en sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        // PASO 2: Si no hay usuario, redirigir al login con mensaje
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para hacer una reservación");
            return "redirect:/login";
        }
        
        // PASO 3: Si hay usuario, mostrar la página
        model.addAttribute("usuario", usuario);
        model.addAttribute("activePage", "reservaciones");
        return "reservacion";
    }

    @GetMapping("/pedidos")
    public String pedidos(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // PASO 1: Verificar si hay usuario en sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        // PASO 2: Si no hay usuario, redirigir al login con mensaje
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para hacer un pedido");
            return "redirect:/login";
        }
        
        // PASO 3: Si hay usuario, mostrar la página
        model.addAttribute("usuario", usuario);
        model.addAttribute("activePage", "pedidos");
        return "pedidos";
    }

    // ========== PROCESAMIENTO DE FORMULARIOS PROTEGIDOS ==========
    
    @PostMapping("/reservaciones/guardar")
    public String guardarReservacion(
            HttpSession session,  // AGREGADO: HttpSession
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam Integer cantidadPersonas,
            @RequestParam(required = false) String notas,
            RedirectAttributes redirectAttributes) {  // CAMBIADO: Model por RedirectAttributes
        
        // PASO 1: Verificar si hay usuario en sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para guardar una reservación");
            return "redirect:/login";
        }
        
        // PASO 2: Aquí iría la lógica para guardar en BD (asociando el usuario)
        System.out.println("Reservación guardada para usuario: " + usuario.getEmail());
        System.out.println("Detalles: " + nombre + " - " + fecha + " " + hora);
        
        // PASO 3: Mensaje de éxito y redirección
        redirectAttributes.addFlashAttribute("mensaje", "¡Reservación realizada con éxito!");
        return "redirect:/reservaciones?exito";
    }

    @PostMapping("/pedidos/guardar")
    public String guardarPedido(
            HttpSession session,  // AGREGADO: HttpSession
            @RequestParam String cliente,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam Double total,
            @RequestParam String metodoEntrega,
            @RequestParam(required = false) String direccion,
            @RequestParam String metodoPago,
            @RequestParam String detalles,
            RedirectAttributes redirectAttributes) {  // CAMBIADO: Model por RedirectAttributes
        
        // PASO 1: Verificar si hay usuario en sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para guardar un pedido");
            return "redirect:/login";
        }
        
        // PASO 2: Aquí iría la lógica para guardar en BD (asociando el usuario)
        System.out.println("Pedido guardado para usuario: " + usuario.getEmail());
        System.out.println("Cliente: " + cliente + " - Total: " + total);
        
        // PASO 3: Mensaje de éxito y redirección
        redirectAttributes.addFlashAttribute("mensaje", "¡Pedido realizado con éxito!");
        return "redirect:/pedidos?exito";
    }

    // ========== FORMULARIO PÚBLICO (NO REQUIERE LOGIN) ==========
    
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
        
        // Si el usuario está logueado, asociarlo automáticamente (opcional)
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            contacto.setUsuario(usuario);
            contacto.setEmail(usuario.getEmail());
        }
        
        // Guardar en la base de datos
        contactoService.guardar(contacto);
        
        System.out.println("Mensaje guardado de " + nombre + " (" + email + "): " + asunto);
        
        redirectAttributes.addFlashAttribute("mensaje", "Mensaje enviado con éxito. Te contactaremos pronto.");
        return "redirect:/contacto?enviado";
    }
}