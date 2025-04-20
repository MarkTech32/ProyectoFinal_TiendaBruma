package com.bruma.controller;

import com.bruma.domain.Carrito;
import com.bruma.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    @Autowired
    private CarritoService carritoService;
    
    @GetMapping("/ver")
    public String verCarrito(HttpSession session, Model model) {
        Carrito carrito = carritoService.getCarrito(session);
        model.addAttribute("carrito", carrito);
        return "/carrito/carrito";
    }
    
    @PostMapping("/agregar/{idProducto}")
    public String agregarProducto(
            @PathVariable("idProducto") Integer idProducto,
            @RequestParam(value = "cantidad", defaultValue = "1") int cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        carritoService.agregarProducto(session, idProducto, cantidad);
        redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito");
        
        return "redirect:/producto/listado";
    }
    
    @GetMapping("/eliminar/{idProducto}")
    public String eliminarProducto(
            @PathVariable("idProducto") Integer idProducto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        carritoService.eliminarProducto(session, idProducto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
        
        return "redirect:/carrito/ver";
    }
    
    @PostMapping("/actualizar/{idProducto}")
    public String actualizarCantidad(
            @PathVariable("idProducto") Integer idProducto,
            @RequestParam("cantidad") int cantidad,
            HttpSession session) {
        
        carritoService.actualizarCantidad(session, idProducto, cantidad);
        
        return "redirect:/carrito/ver";
    }
    
    @GetMapping("/incrementar/{idProducto}")
    public String incrementarCantidad(
            @PathVariable("idProducto") Integer idProducto,
            HttpSession session) {
        
        carritoService.incrementarCantidad(session, idProducto);
        
        return "redirect:/carrito/ver";
    }
    
    @GetMapping("/decrementar/{idProducto}")
    public String decrementarCantidad(
            @PathVariable("idProducto") Integer idProducto,
            HttpSession session) {
        
        carritoService.decrementarCantidad(session, idProducto);
        
        return "redirect:/carrito/ver";
    }
    
    @GetMapping("/vaciar")
    public String vaciarCarrito(
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        carritoService.vaciarCarrito(session);
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado correctamente");
        
        return "redirect:/carrito/ver";
    }
}
