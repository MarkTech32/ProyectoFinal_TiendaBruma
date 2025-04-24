package com.bruma.controller;

import com.bruma.domain.Carrito;
import com.bruma.domain.CarritoItem;
import com.bruma.domain.Direccion;
import com.bruma.domain.Factura;
import com.bruma.domain.MetodoPago;
import com.bruma.domain.Producto;
import com.bruma.domain.Usuario;
import com.bruma.domain.Venta;
import com.bruma.service.CarritoService;
import com.bruma.service.DireccionService;
import com.bruma.service.FacturaService;
import com.bruma.service.MetodoPagoService;
import com.bruma.service.SeguimientoPedidoService;
import com.bruma.service.UsuarioService;
import com.bruma.service.VentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    
    @Autowired
    private DireccionService direccionService;
    
    @Autowired
    private MetodoPagoService metodoPagoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private FacturaService facturaService;
    
    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private CarritoService carritoService;
    
    // Paso 1: Selección de dirección de envío
    @GetMapping("/direcciones")
    public String seleccionarDireccion(Model model, HttpSession session) {
        // Verificar que haya productos en el carrito
        Carrito carrito = carritoService.getCarrito(session);
        if (carrito.getItemCount() == 0) {
            return "redirect:/carrito/ver";
        }
        
        // Obtener usuario autenticado
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener direcciones del usuario
        List<Direccion> direcciones = direccionService.encontrarPorUsuario(usuario.getIdUsuario());
        
        // Si no tiene direcciones, redirigir para crear una
        if (direcciones.isEmpty()) {
            session.setAttribute("checkoutRedirect", "/checkout/direcciones");
            return "redirect:/direcciones/nueva";
        }
        
        // Verificar si ya hay una dirección seleccionada en la sesión
        Integer idDireccionSeleccionada = (Integer) session.getAttribute("checkoutDireccionId");
        
        if (idDireccionSeleccionada == null) {
            // Si no hay dirección seleccionada, intentar usar la predeterminada
            Direccion direccionPrincipal = direccionService.encontrarPrincipal(usuario.getIdUsuario());
            if (direccionPrincipal != null) {
                idDireccionSeleccionada = direccionPrincipal.getIdDireccion();
                session.setAttribute("checkoutDireccionId", idDireccionSeleccionada);
            }
        }
        
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("direccionSeleccionadaId", idDireccionSeleccionada);
        model.addAttribute("carrito", carrito);
        
        return "checkout/seleccion-direccion";
    }
    
    // Procesar selección de dirección
    @PostMapping("/seleccionar-direccion")
    public String procesarDireccion(@RequestParam("direccionId") Integer direccionId, HttpSession session) {
        // Guardar la dirección seleccionada en la sesión
        session.setAttribute("checkoutDireccionId", direccionId);
        
        // Redirigir al siguiente paso
        return "redirect:/checkout/metodos-pago";
    }
    
    // Paso 2: Selección de método de pago
    @GetMapping("/metodos-pago")
    public String seleccionarMetodoPago(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Verificar que se haya seleccionado una dirección
        Integer idDireccionSeleccionada = (Integer) session.getAttribute("checkoutDireccionId");
        if (idDireccionSeleccionada == null) {
            return "redirect:/checkout/direcciones";
        }
        
        // Obtener usuario autenticado
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener métodos de pago del usuario
        List<MetodoPago> metodosPago = metodoPagoService.encontrarPorUsuario(usuario.getIdUsuario());
        
        // Si no tiene métodos de pago, redirigir para crear uno
        if (metodosPago.isEmpty()) {
            session.setAttribute("checkoutRedirect", "/checkout/metodos-pago");
            return "redirect:/metodos-pago/nuevo";
        }
        
        // Verificar si ya hay un método de pago seleccionado en la sesión
        Integer idMetodoPagoSeleccionado = (Integer) session.getAttribute("checkoutMetodoPagoId");
        
        if (idMetodoPagoSeleccionado == null) {
            // Si no hay método seleccionado, intentar usar el predeterminado
            MetodoPago metodoPrincipal = metodoPagoService.encontrarPrincipal(usuario.getIdUsuario());
            if (metodoPrincipal != null) {
                idMetodoPagoSeleccionado = metodoPrincipal.getIdMetodoPago();
                session.setAttribute("checkoutMetodoPagoId", idMetodoPagoSeleccionado);
            }
        }
        
        // Obtener dirección seleccionada para mostrarla
        Direccion direccionSeleccionada = direccionService.encontrarPorId(idDireccionSeleccionada);
        
        model.addAttribute("metodosPago", metodosPago);
        model.addAttribute("metodoPagoSeleccionadoId", idMetodoPagoSeleccionado);
        model.addAttribute("direccionSeleccionada", direccionSeleccionada);
        model.addAttribute("carrito", carritoService.getCarrito(session));
        
        return "checkout/seleccion-metodo-pago";
    }
    
    // Procesar selección de método de pago
    @PostMapping("/seleccionar-metodo-pago")
    public String procesarMetodoPago(@RequestParam("metodoPagoId") Integer metodoPagoId, HttpSession session) {
        // Guardar el método de pago seleccionado en la sesión
        session.setAttribute("checkoutMetodoPagoId", metodoPagoId);
        
        // Redirigir al siguiente paso
        return "redirect:/checkout/confirmar";
    }
    
    // Paso 3: Confirmación del pedido
    @GetMapping("/confirmar")
    public String confirmarPedido(Model model, HttpSession session) {
        // Verificar que se haya seleccionado dirección y método de pago
        Integer idDireccionSeleccionada = (Integer) session.getAttribute("checkoutDireccionId");
        Integer idMetodoPagoSeleccionado = (Integer) session.getAttribute("checkoutMetodoPagoId");
        
        if (idDireccionSeleccionada == null) {
            return "redirect:/checkout/direcciones";
        }
        
        if (idMetodoPagoSeleccionado == null) {
            return "redirect:/checkout/metodos-pago";
        }
        
        // Obtener usuario autenticado
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener dirección y método de pago seleccionados
        Direccion direccion = direccionService.encontrarPorId(idDireccionSeleccionada);
        MetodoPago metodoPago = metodoPagoService.encontrarPorId(idMetodoPagoSeleccionado);
        
        // Obtener el carrito
        Carrito carrito = carritoService.getCarrito(session);
        
        model.addAttribute("direccion", direccion);
        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("carrito", carrito);
        
        return "checkout/confirmar";
    }
    
    // Procesar el pedido (crear factura)
    @PostMapping("/procesar")
    public String procesarPedido(HttpSession session, RedirectAttributes redirectAttributes) {
        // Obtener usuario autenticado
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener dirección y método de pago seleccionados
        Integer idDireccion = (Integer) session.getAttribute("checkoutDireccionId");
        Integer idMetodoPago = (Integer) session.getAttribute("checkoutMetodoPagoId");
        
        if (idDireccion == null || idMetodoPago == null) {
            return "redirect:/checkout/direcciones";
        }
        
        Direccion direccion = direccionService.encontrarPorId(idDireccion);
        MetodoPago metodoPago = metodoPagoService.encontrarPorId(idMetodoPago);
        
        // Obtener el carrito
        Carrito carrito = carritoService.getCarrito(session);
        
        // Verificar disponibilidad de productos
        for (CarritoItem item : carrito.getItems()) {
            Producto producto = item.getProducto();
            int cantidad = item.getCantidad();
            
            if (producto.getExistencias() < cantidad) {
                redirectAttributes.addFlashAttribute("error", 
                    "No hay suficiente stock para " + producto.getNombre());
                return "redirect:/carrito/ver";
            }
        }
        
        // Crear la factura
        Factura factura = facturaService.crearFactura(usuario, direccion, metodoPago, new ArrayList<>(), carrito.getTotal());
        
        // Crear las ventas (items de la factura)
        List<Venta> ventas = ventaService.crearVentasDesdeCarrito(factura, carrito);
        
        // Limpieza de sesión
        carritoService.vaciarCarrito(session);
        session.removeAttribute("checkoutDireccionId");
        session.removeAttribute("checkoutMetodoPagoId");
        
        // Mensaje de éxito
        redirectAttributes.addFlashAttribute("mensaje", "¡Tu pedido ha sido procesado con éxito!");
        
        // Redirigir a la página de factura
        return "redirect:/factura/detalle/" + factura.getIdFactura();
    }
    
    // Método auxiliar para obtener el usuario actualmente autenticado
    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        String username;
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        return usuarioService.encontrarPorUsername(username);
    }
}