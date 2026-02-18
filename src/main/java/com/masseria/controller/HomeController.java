package com.masseria.controller;

import com.masseria.entity.Contacto;
import com.masseria.entity.Pedido;
import com.masseria.entity.Reservacion;
import com.masseria.entity.Usuario;
import com.masseria.service.ContactoService;
import com.masseria.service.PedidoService;  // <-- AGREGAR IMPORT
import com.masseria.service.ProductoService;
import com.masseria.service.CategoriaService;
import com.masseria.service.ReservacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private ContactoService contactoService;
    
    @Autowired
    private ReservacionService reservacionService;
    
    @Autowired
    private PedidoService pedidoService;  // <-- AGREGAR

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
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para hacer una reservación");
            return "redirect:/login?acceso=denegado";
        }
        
        // ===== DATOS PARA PRE-LLENAR EL FORMULARIO =====
        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreUsuario", usuario.getNombres() + " " + usuario.getApellidos());
        model.addAttribute("emailUsuario", usuario.getEmail());
        model.addAttribute("telefonoUsuario", usuario.getTelefono() != null ? usuario.getTelefono() : "");
        
        // Fecha mínima (hoy)
        model.addAttribute("fechaMinima", LocalDate.now().toString());
        
        // Horarios disponibles
        List<String> horarios = List.of(
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", 
            "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00"
        );
        model.addAttribute("horarios", horarios);
        
        model.addAttribute("activePage", "reservaciones");
        return "reservacion";
    }

    @GetMapping("/pedidos")
    public String pedidos(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para hacer un pedido");
            return "redirect:/login?acceso=denegado";
        }
        
        // ===== DATOS PARA PRE-LLENAR EL FORMULARIO =====
        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreCompleto", usuario.getNombres() + " " + usuario.getApellidos());
        model.addAttribute("email", usuario.getEmail());
        model.addAttribute("telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "");
        model.addAttribute("direccion", usuario.getDireccion() != null ? usuario.getDireccion() : "");
        
        model.addAttribute("activePage", "pedidos");
        return "pedidos";
    }

    // ========== PROCESAMIENTO DE FORMULARIOS PROTEGIDOS ==========
    
    @PostMapping("/reservaciones/guardar")
    public String guardarReservacion(
            HttpSession session,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam Integer cantidadPersonas,
            @RequestParam(required = false) String notas,
            RedirectAttributes redirectAttributes) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para guardar una reservación");
            return "redirect:/login";
        }
        
        try {
            // ===== CREAR Y GUARDAR LA RESERVACIÓN =====
            Reservacion reservacion = new Reservacion();
            reservacion.setNombre(usuario.getNombres() + " " + usuario.getApellidos());
            reservacion.setEmail(usuario.getEmail());
            reservacion.setTelefono(telefono);
            reservacion.setFecha(LocalDate.parse(fecha));
            reservacion.setHora(LocalTime.parse(hora));
            reservacion.setCantidadPersonas(cantidadPersonas);
            reservacion.setNotas(notas);
            reservacion.setUsuario(usuario);
            reservacion.setEstado("PENDIENTE");
            
            // Verificar disponibilidad
            if (!reservacionService.verificarDisponibilidad(LocalDate.parse(fecha), LocalTime.parse(hora))) {
                redirectAttributes.addFlashAttribute("error", "Lo sentimos, ese horario ya no está disponible");
                return "redirect:/reservaciones?error=horario";
            }
            
            reservacionService.guardar(reservacion);
            
            System.out.println("✅ Reservación guardada - ID: " + reservacion.getId());
            System.out.println("   Usuario: " + usuario.getEmail());
            System.out.println("   Fecha: " + fecha + " " + hora);
            System.out.println("   Personas: " + cantidadPersonas);
            
            redirectAttributes.addFlashAttribute("mensaje", "¡Reservación realizada con éxito! Te esperamos.");
            return "redirect:/reservaciones?exito";
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar reservación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al procesar la reservación: " + e.getMessage());
            return "redirect:/reservaciones?error";
        }
    }

    @PostMapping("/pedidos/guardar")
    public String guardarPedido(
            HttpSession session,
            @RequestParam String cliente,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam Double total,
            @RequestParam String metodoEntrega,
            @RequestParam(required = false) String direccion,
            @RequestParam String metodoPago,
            @RequestParam String detalles,
            RedirectAttributes redirectAttributes) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para guardar un pedido");
            return "redirect:/login";
        }
        
        try {
            // ===== CREAR EL PEDIDO CON LOS DATOS DEL USUARIO =====
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setClienteNombre(usuario.getNombres() + " " + usuario.getApellidos());
            pedido.setClienteEmail(usuario.getEmail());
            pedido.setClienteTelefono(telefono);
            pedido.setTipoEntrega(metodoEntrega);
            pedido.setMetodoPago(metodoPago);
            pedido.setNotas(detalles);
            pedido.setTotal(BigDecimal.valueOf(total != null ? total : 0.0));
            
            // Si es envío a domicilio, guardar dirección
            if ("DOMICILIO".equals(metodoEntrega) && direccion != null && !direccion.isEmpty()) {
                pedido.setDireccionEntrega(direccion);
            } else if ("RECOGER".equals(metodoEntrega)) {
                pedido.setDireccionEntrega("RECOGER EN TIENDA");
            }
            
            // Guardar el pedido
            pedidoService.guardar(pedido);
            
            System.out.println("✅ Pedido guardado - ID: " + pedido.getId());
            System.out.println("   Usuario: " + usuario.getEmail());
            System.out.println("   Total: S/ " + total);
            System.out.println("   Tipo entrega: " + metodoEntrega);
            
            redirectAttributes.addFlashAttribute("mensaje", "¡Pedido realizado con éxito! Te contactaremos pronto.");
            return "redirect:/pedidos?exito";
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar pedido: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pedido: " + e.getMessage());
            return "redirect:/pedidos?error";
        }
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
        
        // Si el usuario está logueado, asociarlo automáticamente
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            contacto.setUsuario(usuario);
            contacto.setEmail(usuario.getEmail());
        }
        
        contactoService.guardar(contacto);
        
        System.out.println("📧 Mensaje guardado de " + nombre + " (" + email + "): " + asunto);
        
        redirectAttributes.addFlashAttribute("mensaje", "Mensaje enviado con éxito. Te contactaremos pronto.");
        return "redirect:/contacto?enviado";
    }
}