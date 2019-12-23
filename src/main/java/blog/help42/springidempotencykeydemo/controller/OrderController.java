package blog.help42.springidempotencykeydemo.controller;


import blog.help42.springidempotencykeydemo.model.order.Order;
import blog.help42.springidempotencykeydemo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Transactional
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        // create or select an account

        // create order

//        try {
        order = orderRepository.save(order);
//        } catch (DataIntegrityViolationException e) {
//            throw handleDataIntegrityViolationException(e, order.getIdempotenceKey());
//        }

        //Create resource location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        //Send location in response
        return ResponseEntity.created(location).body(order);
    }
}
