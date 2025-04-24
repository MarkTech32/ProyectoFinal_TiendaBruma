package com.bruma.controller;

import com.bruma.domain.Factura;
import com.bruma.domain.SeguimientoPedido;
import com.bruma.domain.Usuario;
import com.bruma.domain.Venta;
import com.bruma.service.FacturaService;
import com.bruma.service.SeguimientoPedidoService;
import com.bruma.service.UsuarioService;
import com.bruma.service.VentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/factura")
public class FacturaController {
    
    @Autowired
    private FacturaService facturaService;
    
    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private SeguimientoPedidoService seguimientoPedidoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    // Mostrar listado de facturas del usuario
    @GetMapping("/mis-pedidos")
    public String misPedidos(Model model) {
        // Obtener usuario autenticado
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener las facturas del usuario
        List<Factura> facturas = facturaService.encontrarPorUsuario(usuario.getIdUsuario());
        
        model.addAttribute("facturas", facturas);
        return "factura/mis-pedidos";
    }
    
    // Mostrar detalle de una factura específica para el usuario
    @GetMapping("/detalle/{id}")
    public String detalleFactura(@PathVariable("id") Integer idFactura, Model model, RedirectAttributes redirectAttributes) {
        // Obtener usuario autenticado
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener la factura
        Factura factura = facturaService.encontrarPorId(idFactura);
        
        // Verificar que la factura exista
        if (factura == null) {
            redirectAttributes.addFlashAttribute("error", "Factura no encontrada");
            return "redirect:/factura/mis-pedidos";
        }
        
        // Verificar que la factura pertenezca al usuario o que el usuario sea admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        if (!factura.getUsuario().getIdUsuario().equals(usuario.getIdUsuario()) && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver esta factura");
            return "redirect:/factura/mis-pedidos";
        }
        
        // Obtener los items de la factura
        List<Venta> items = ventaService.encontrarPorFactura(idFactura);
        
        // Obtener el historial de seguimiento
        List<SeguimientoPedido> seguimientos = seguimientoPedidoService.encontrarPorFactura(idFactura);
        
        model.addAttribute("factura", factura);
        model.addAttribute("items", items);
        model.addAttribute("seguimientos", seguimientos);
        model.addAttribute("puedesCancelar", factura.sePuedeCancelar());
        
        return "factura/detalle";
    }
    
    // Mostrar seguimiento de un pedido
    @GetMapping("/seguimiento/{id}")
    public String seguimientoPedido(@PathVariable("id") Integer idFactura, Model model, RedirectAttributes redirectAttributes) {
        // Obtener usuario autenticado
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener la factura
        Factura factura = facturaService.encontrarPorId(idFactura);
        
        // Verificar que la factura exista
        if (factura == null) {
            redirectAttributes.addFlashAttribute("error", "Factura no encontrada");
            return "redirect:/factura/mis-pedidos";
        }
        
        // Verificar que la factura pertenezca al usuario o que el usuario sea admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        if (!factura.getUsuario().getIdUsuario().equals(usuario.getIdUsuario()) && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver esta factura");
            return "redirect:/factura/mis-pedidos";
        }
        
        // Obtener el historial de seguimiento
        List<SeguimientoPedido> seguimientos = seguimientoPedidoService.encontrarPorFactura(idFactura);
        
        model.addAttribute("factura", factura);
        model.addAttribute("seguimientos", seguimientos);
        
        return "factura/seguimiento";
    }
    
    // Cancelar un pedido
    @PostMapping("/cancelar/{id}")
    public String cancelarPedido(@PathVariable("id") Integer idFactura, 
                                @RequestParam("motivo") String motivo,
                                RedirectAttributes redirectAttributes) {
        // Obtener usuario autenticado
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener la factura
        Factura factura = facturaService.encontrarPorId(idFactura);
        
        // Verificar que la factura exista
        if (factura == null) {
            redirectAttributes.addFlashAttribute("error", "Factura no encontrada");
            return "redirect:/factura/mis-pedidos";
        }
        
        // Verificar que la factura pertenezca al usuario
        if (!factura.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para cancelar esta factura");
            return "redirect:/factura/mis-pedidos";
        }
        
        // Verificar que se pueda cancelar
        if (!factura.sePuedeCancelar()) {
            redirectAttributes.addFlashAttribute("error", "Este pedido ya no puede ser cancelado");
            return "redirect:/factura/detalle/" + idFactura;
        }
        
        // Procesar la cancelación
        boolean cancelado = facturaService.cancelarFactura(factura, motivo);
        
        if (cancelado) {
            redirectAttributes.addFlashAttribute("mensaje", "Pedido cancelado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo cancelar el pedido");
        }
        
        return "redirect:/factura/detalle/" + idFactura;
    }
    
    // ====== ADMINISTRACIÓN DE PEDIDOS ======
    
    // Método para administradores: ver todos los pedidos
    @GetMapping("/admin-pedidos")
public String listadoAdminPedidos(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "idFactura", required = false) Integer idFactura,
        @RequestParam(name = "usuario", required = false) String usuario,
        @RequestParam(name = "estado", required = false) Integer estado,
        @RequestParam(name = "fechaDesde", required = false) String fechaDesde,
        Model model, RedirectAttributes redirectAttributes) {
    
    // Verificar que el usuario sea admin
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
        return "redirect:/";
    }
    
    try {
        // Obtener las facturas filtradas
        List<Factura> facturas;
        
        // Aplicar filtros si se han especificado
        if (idFactura != null) {
            // Buscar por ID específico
            Factura factura = facturaService.encontrarPorId(idFactura);
            facturas = factura != null ? List.of(factura) : List.of();
        } else if (usuario != null && !usuario.isEmpty()) {
            // Buscar por nombre de usuario
            facturas = facturaService.encontrarPorNombreUsuario(usuario);
        } else if (estado != null) {
            // Buscar por estado
            facturas = facturaService.encontrarPorEstado(estado);
        } else if (fechaDesde != null && !fechaDesde.isEmpty()) {
            // Buscar por fecha
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date fecha = dateFormat.parse(fechaDesde);
                facturas = facturaService.encontrarDesdeFecha(fecha);
            } catch (ParseException e) {
                facturas = facturaService.encontrarTodas();
            }
        } else {
            // Si no hay filtros, mostrar todas las facturas
            facturas = facturaService.encontrarTodas();
        }
        
        // Agregar atributos para la paginación (simplificado)
        model.addAttribute("facturas", facturas);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (facturas.size() + size - 1) / size); // Cálculo básico de páginas
        model.addAttribute("size", size);
        
        return "factura/admin-pedidos";
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al cargar los pedidos: " + e.getMessage());
        return "redirect:/";
    }
}
    
    // Método para administradores: ver detalle de un pedido
    @GetMapping("/admin-detalle/{id}")
    public String detalleAdminPedido(@PathVariable("id") Integer idFactura, Model model, RedirectAttributes redirectAttributes) {
        // Verificar que el usuario sea admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/";
        }
        
        // Obtener la factura
        Factura factura = facturaService.encontrarPorId(idFactura);
        
        // Verificar que la factura exista
        if (factura == null) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/factura/admin-pedidos";
        }
        
        // Obtener los items de la factura
        List<Venta> items = ventaService.encontrarPorFactura(idFactura);
        
        // Obtener el historial de seguimiento
        List<SeguimientoPedido> seguimientos = seguimientoPedidoService.encontrarPorFactura(idFactura);
        
        model.addAttribute("factura", factura);
        model.addAttribute("items", items);
        model.addAttribute("seguimientos", seguimientos);
        
        return "factura/admin-detalle";
    }
    
    // Método para administradores: actualizar estado de un pedido
    @PostMapping("/actualizar-estado/{id}")
    public String actualizarEstadoPedido(
            @PathVariable("id") Integer idFactura,
            @RequestParam("nuevoEstado") Integer nuevoEstado,
            @RequestParam("comentario") String comentario,
            @RequestParam(value = "fechaEnvio", required = false) String fechaEnvio,
            @RequestParam(value = "fechaEntrega", required = false) String fechaEntrega,
            @RequestParam(value = "numeroSeguimiento", required = false) String numeroSeguimiento,
            @RequestParam(value = "motivoCancelacion", required = false) String motivoCancelacion,
            RedirectAttributes redirectAttributes) {
        
        // Verificar que el usuario sea admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/";
        }
        
        try {
            // Obtener la factura
            Factura factura = facturaService.encontrarPorId(idFactura);
            
            // Verificar que la factura exista
            if (factura == null) {
                redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
                return "redirect:/factura/admin-pedidos";
            }
            
            // Actualizar el estado
            factura.setEstado(nuevoEstado);
            
            // Actualizar fechas y datos adicionales según estado
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            if (nuevoEstado == 2) { // Confirmado
                factura.setFechaConfirmacion(new Date());
            } else if (nuevoEstado == 4) { // Enviado
                if (fechaEnvio != null && !fechaEnvio.isEmpty()) {
                    try {
                        factura.setFechaEnvio(dateFormat.parse(fechaEnvio));
                    } catch (ParseException e) {
                        // Si hay error en formato, usar fecha actual
                        factura.setFechaEnvio(new Date());
                    }
                } else {
                    factura.setFechaEnvio(new Date());
                }
                
                if (fechaEntrega != null && !fechaEntrega.isEmpty()) {
                    try {
                        factura.setFechaEntregaEstimada(dateFormat.parse(fechaEntrega));
                    } catch (ParseException e) {
                        // Si hay error, no establecer fecha estimada
                    }
                }
                
                // Aquí podrías guardar el número de seguimiento si tienes un campo para ello
                // factura.setNumeroSeguimiento(numeroSeguimiento);
            } else if (nuevoEstado == 5) { // Entregado
                factura.setFechaEntregaReal(new Date());
            }
            
            // Si es cancelación, preparar comentario adecuado
            if (nuevoEstado == 6 && motivoCancelacion != null && !motivoCancelacion.isEmpty()) {
                comentario = "Motivo de cancelación: " + motivoCancelacion + 
                             (comentario != null && !comentario.isEmpty() ? ". " + comentario : "");
            }
            
            // Guardar factura
            facturaService.guardar(factura);
            
            // Registrar el cambio en el seguimiento
            SeguimientoPedido seguimiento = new SeguimientoPedido();
            seguimiento.setFactura(factura);
            seguimiento.setEstado(factura.getEstadoTexto());
            seguimiento.setComentario(comentario);
            seguimiento.setFechaCambio(new Date());
            seguimientoPedidoService.registrarCambioEstado(factura, factura.getEstadoTexto(), comentario);
            
            redirectAttributes.addFlashAttribute("mensaje", "Estado del pedido actualizado correctamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el estado: " + e.getMessage());
        }
        
        return "redirect:/factura/admin-detalle/" + idFactura;
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