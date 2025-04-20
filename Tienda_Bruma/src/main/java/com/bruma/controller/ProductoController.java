package com.bruma.controller;

import com.bruma.domain.Carrito;
import com.bruma.domain.Producto;
import com.bruma.service.CarritoService;
import com.bruma.service.CategoriaService;
import com.bruma.service.ProductoService;
import com.bruma.service.FirebaseStorageService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private CarritoService carritoService;
    
    @GetMapping("/listado")
    public String listado(Model model, HttpSession session) {
        var productos = productoService.getProductos(false);
        var categorias = categoriaService.getCategorias(true);
        
        // Agregar el carrito al modelo
        Carrito carrito = carritoService.getCarrito(session);
        
        // Calcular cantidades en carrito
        Map<Integer, Integer> cantidadesEnCarrito = calcularCantidadesEnCarrito(carrito);

        // Calcular cantidades disponibles
        Map<Integer, Integer> cantidadesDisponibles = calcularCantidadesDisponibles(productos, cantidadesEnCarrito);

        // Agregar al modelo
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("carrito", carrito);
        model.addAttribute("cantidadesEnCarrito", cantidadesEnCarrito);
        model.addAttribute("cantidadesDisponibles", cantidadesDisponibles);
        
        return "/producto/listado";
    }
    
    @GetMapping("/listado/{idCategoria}")
    public String listadoPorCategoria(@PathVariable("idCategoria") Integer idCategoria, Model model, HttpSession session) {
        var productos = productoService.getProductosPorCategoria(idCategoria);
        var categorias = categoriaService.getCategorias(true);
        
        // Agregar el carrito al modelo
        Carrito carrito = carritoService.getCarrito(session);
        
        // Calcular cantidades en carrito
        Map<Integer, Integer> cantidadesEnCarrito = calcularCantidadesEnCarrito(carrito);

        // Calcular cantidades disponibles
        Map<Integer, Integer> cantidadesDisponibles = calcularCantidadesDisponibles(productos, cantidadesEnCarrito);

        // Agregar al modelo
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("carrito", carrito);
        model.addAttribute("cantidadesEnCarrito", cantidadesEnCarrito);
        model.addAttribute("cantidadesDisponibles", cantidadesDisponibles);
        
        return "/producto/listado";
    }
    
       // Método para calcular cantidades en carrito por producto
    private Map<Integer, Integer> calcularCantidadesEnCarrito(Carrito carrito) {
        Map<Integer, Integer> cantidades = new HashMap<>();

        if (carrito != null && carrito.getItems() != null && !carrito.getItems().isEmpty()) {
            carrito.getItems().forEach(item -> {
                if (item.getProducto() != null && item.getProducto().getIdProducto() != null) {
                    cantidades.put(item.getProducto().getIdProducto(), item.getCantidad());
                }
            });
        }

        return cantidades;
    }

    // Método para calcular cantidades disponibles (existencias - enCarrito)
    private Map<Integer, Integer> calcularCantidadesDisponibles(Iterable<Producto> productos, Map<Integer, Integer> cantidadesEnCarrito) {
        Map<Integer, Integer> disponibles = new HashMap<>();

        for (Producto producto : productos) {
            int enCarrito = cantidadesEnCarrito.getOrDefault(producto.getIdProducto(), 0);
            disponibles.put(producto.getIdProducto(), producto.getExistencias() - enCarrito);
        }

        return disponibles;
    }
    
    @GetMapping("/nuevo")
    public String productoNuevo(Producto producto, Model model) {
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "/producto/form";
    }
    
    @GetMapping("/eliminar/{idProducto}")
    public String eliminar(@PathVariable("idProducto") Integer idProducto) {
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        
        productoService.delete(producto);
        return "redirect:/producto/listado";
    }
    
    @GetMapping("/modificar/{idProducto}")
    public String modificar(@PathVariable("idProducto") Integer idProducto, Model model) {
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        
        producto = productoService.getProducto(producto);
        model.addAttribute("producto", producto);
        
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        
        return "/producto/form";
    }
    
    //Implementacion de Fireship - Completada
    @Autowired 
    private FirebaseStorageService firebaseStorageService;
    
    @PostMapping("/guardar")
    public String guardar(Producto producto, 
            @RequestParam("imagenFile") MultipartFile imagenFile){
        if(!imagenFile.isEmpty()){
            //Nos pasan una imagen
            productoService.save(producto);
            String ruta = firebaseStorageService.cargaImagen(imagenFile,
                    "producto", producto.getIdProducto());
            producto.setRutaImagen(ruta);
        }
        productoService.save(producto);
        return "redirect:/producto/listado";
    }
}