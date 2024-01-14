package com.msvc.order.service;

import brave.Span;
import brave.Tracer;
import com.msvc.order.dto.OrderLineItemsDto;
import com.msvc.order.dto.OrderRequest;
import com.msvc.order.dto.inventario.InventarioResponse;
import com.msvc.order.event.OrderPlacedEvent;
import com.msvc.order.model.Order;
import com.msvc.order.model.OrderLineItems;
import com.msvc.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.sleuth.Span;
//import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderService {

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;

    //@Autowired
    //private WebClient webClient;

    // use for load balancing
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private Tracer tracer;

    //@Transactional(readOnly = true)
    @Transactional
    public String placeOrder(OrderRequest orderRequest){
        log.info("Crear nueva orden");
        Order order = new Order();
        order.setNumeroPedido(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtos()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        order.setOrderLineItems(orderLineItemsList);

        List<String> codigosSku = order.getOrderLineItems().stream()
                        .map(OrderLineItems::getCodigoSku)
                                .collect(Collectors.toList());

        System.out.println("Codigos sku: " + codigosSku);
        log.info("Codigos sku: " + codigosSku);

        Span inventarioServiceLooking = tracer.nextSpan().name("InventarioServiceLooking");

        try (Tracer.SpanInScope isLookup = tracer.withSpanInScope(inventarioServiceLooking.start())){
            inventarioServiceLooking.tag("call", "inventario-service");

            //InventarioResponse[] inventarioResponses = webClient
            InventarioResponse[] inventarioResponses = webClientBuilder.build()
                    .get()
                    //.uri("http://192.168.1.3:8082/api/inventario",
                    .uri("http://inventario-service/api/inventario",
                            uriBuilder -> uriBuilder.queryParam("codigosSku", codigosSku)
                                    .build())
                    .retrieve()
                    .bodyToMono(InventarioResponse[].class)
                    .block();

            boolean allProductosInStock = Arrays.stream(inventarioResponses).allMatch(InventarioResponse::isInStock);
            if(allProductosInStock){
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getNumeroPedido()));
                return "Pedido creado con exito";
            } else {
                throw new IllegalArgumentException("El producto no esta en stock");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inventarioServiceLooking.flush();
        }

        return "Error";
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrecio(orderLineItemsDto.getPrecio());
        orderLineItems.setCantidad(orderLineItemsDto.getCantidad());
        orderLineItems.setCodigoSku(orderLineItemsDto.getCodigoSku());
        return orderLineItems;
    }
}
