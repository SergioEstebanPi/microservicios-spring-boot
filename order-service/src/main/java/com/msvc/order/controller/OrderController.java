package com.msvc.order.controller;

import brave.Span;
import brave.Tracer;
import com.msvc.order.dto.OrderRequest;
import com.msvc.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    Tracer tracer;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackInventario")
    //@TimeLimiter(name = "inventario")
    //@Retry(name = "inventario")
    public CompletableFuture<String>  realizarPedido(@RequestBody OrderRequest orderRequest){
        //orderService.placeOrder(orderRequest);
        //return "Pedido realizado con exito";
        log.info("Metodo realizarPedido");
        Span span = tracer.currentSpan();
        log.info("first");
        return CompletableFuture.supplyAsync(() -> {
            try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
                log.info("second");
                return orderService.placeOrder(orderRequest);
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        });
    }

    public CompletableFuture<String> fallbackInventario(OrderRequest orderRequest, RuntimeException exception){
        return CompletableFuture.supplyAsync(() -> "Oops ha ocurrido un error al realizar el pedido");
    }

}
