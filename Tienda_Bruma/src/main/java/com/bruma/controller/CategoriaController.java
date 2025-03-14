package com.bruma.controller;

import com.bruma.domain.Categoria;
import com.bruma.service.CategoriaService;
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
@RequestMapping("/categoria")
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping("/listado")
    public String listado(Model model){
        var lista = categoriaService.getCategorias(false);
        model.addAttribute("categorias", lista);
        model.addAttribute("totalCategorias", lista.size());
        return "/categoria/listado";
    }
    
    @GetMapping("/eliminar/{idCategoria}")
    public String eliminar(Categoria categoria){
       categoriaService.delete(categoria);
       return "redirect:/categoria/listado";
    }
    
    @GetMapping("/modificar/{idCategoria}")
    public String modificar(Categoria categoria, Model model){
       categoria = categoriaService.getCategoria(categoria);
       model.addAttribute("categoria", categoria);
       return "/categoria/modifica";
    }
    
    /* ---Codigo para una futura implementacion de Fireship----
    @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    @PostMapping("/guardar")
    public String guardar(Categoria categoria, 
            @RequestParam("imagenFile") MultipartFile imagenFile){
        if(!imagenFile.isEmpty()){
            //Nos pasan una imagen
            categoriaService.save(categoria);
            String ruta = firebaseStorageService.cargaImagen(imagenFile, 
                    "categoria", categoria.getIdCategoria());
            categoria.setRutaImagen(ruta);
        }
        categoriaService.save(categoria);
        return "redirect:/categoria/listado";
    }*/
}