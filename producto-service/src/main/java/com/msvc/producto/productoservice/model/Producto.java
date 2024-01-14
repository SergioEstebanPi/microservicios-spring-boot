package com.msvc.producto.productoservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "producto")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Producto {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
}
