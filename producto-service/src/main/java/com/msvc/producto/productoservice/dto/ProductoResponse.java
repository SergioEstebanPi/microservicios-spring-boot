package com.msvc.producto.productoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductoResponse {
    private String id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
}
