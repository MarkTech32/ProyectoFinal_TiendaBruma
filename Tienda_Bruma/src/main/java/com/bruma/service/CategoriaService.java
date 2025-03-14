package com.bruma.service;

import com.bruma.domain.Categoria;
import com.bruma.repository.CategoriaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    
    @Transactional(readOnly=true)
    public List<Categoria> getCategorias(boolean activos){
        var lista = categoriaRepository.findAll();
        
        
        return lista;
    }
    
    //Metodo de Get
    @Transactional(readOnly=true)
    public Categoria getCategoria(Categoria categoria){
        categoria = categoriaRepository.findById(categoria.getIdCategoria()).orElse(null);
        
        return categoria;
    }
    
    //Metodos de Delete
    @Transactional
    public void delete(Categoria categoria){
        //Si el idCategoria pasado existe, se elimina ese registro
         categoriaRepository.delete(categoria);
        
    }
    
    //Metodos de Save
    @Transactional
    public void save(Categoria categoria){
        //Si el objeto categoria tiene info en idCategoria, se actualiza el registro
        //Si el objeto categoria NO tiene info en idCategoria, se inserta un nuevo registro
         categoriaRepository.save(categoria);
        
    }
}
