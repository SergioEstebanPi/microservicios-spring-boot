package com.msvc.producto.productoservice.controller;

import com.msvc.producto.productoservice.dto.ProductoRequest;
import com.msvc.producto.productoservice.dto.ProductoResponse;
import com.msvc.producto.productoservice.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void guardarProducto(@RequestBody ProductoRequest productoRequest){
        productoService.createProducto(productoRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductoResponse> listarProductos(){
        return productoService.getAllProductos();
    }
}
