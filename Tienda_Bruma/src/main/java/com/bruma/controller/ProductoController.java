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

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping("/listado")
    public String listado(Model model){
        var lista = productoService.getProductos(false);
        model.addAttribute("productos", lista);
        model.addAttribute("totalProductos", lista.size());
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "/producto/listado";
    }
    
    @GetMapping("/eliminar/{idProducto}")
    public String eliminar(Producto producto){
       productoService.delete(producto);
       return "redirect:/producto/listado";
    }
        
    @GetMapping("/modificar/{idProducto}")
    public String modificar(Producto producto, Model model){
       producto = productoService.getProducto(producto);
       model.addAttribute("producto", producto);
       var categorias = categoriaService.getCategorias(true);
       model.addAttribute("categorias", categorias);
       return "/producto/modifica";
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