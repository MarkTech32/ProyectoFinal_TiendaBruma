package com.bruma.controller;

import com.bruma.domain.Producto;
import com.bruma.service.CategoriaService;
import com.bruma.service.ProductoService;
//import com.bruma.service.FirebaseStorageService;
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
    
    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        var categorias = categoriaService.getCategorias(true);
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalProductos", productos.size());
        
        return "/producto/listado";
    }
    
    @GetMapping("/listado/{idCategoria}")
    public String listadoPorCategoria(@PathVariable("idCategoria") Long idCategoria, Model model) {
        var productos = productoService.getProductosPorCategoria(idCategoria);
        var categorias = categoriaService.getCategorias(true);
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalProductos", productos.size());
        
        return "/producto/listado";
    }
    
    @GetMapping("/nuevo")
    public String productoNuevo(Producto producto, Model model) {
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "/producto/form";
    }
    
    @GetMapping("/eliminar/{idProducto}")
    public String eliminar(@PathVariable("idProducto") Long idProducto) {
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        
        productoService.delete(producto);
        return "redirect:/producto/listado";
    }
    
    @GetMapping("/modificar/{idProducto}")
    public String modificar(@PathVariable("idProducto") Long idProducto, Model model) {
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        
        producto = productoService.getProducto(producto);
        model.addAttribute("producto", producto);
        
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        
        return "/producto/form";
    }
    
    @PostMapping("/guardar")
    public String guardar(Producto producto) {
        // Por ahora, guardamos el producto sin manejar imágenes
        productoService.save(producto);
        
        return "redirect:/producto/listado";
    }
    
    /* ---Codigo para una futura implementacion de Fireship----
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
    }*/
}