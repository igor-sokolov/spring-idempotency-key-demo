package blog.help42.springidempotencykeydemo.controller;

import blog.help42.springidempotencykeydemo.exception.IdempotencyKeyException;
import blog.help42.springidempotencykeydemo.model.order.Account;
import blog.help42.springidempotencykeydemo.model.order.Address;
import blog.help42.springidempotencykeydemo.model.order.Order;
import blog.help42.springidempotencykeydemo.repository.AccountRepository;
import blog.help42.springidempotencykeydemo.repository.AddressRepository;
import blog.help42.springidempotencykeydemo.repository.OrderRepository;
import blog.help42.springidempotencykeydemo.utils.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping(path = OrderController.RESOURCE_PATH)
public class OrderController {
    public static final String RESOURCE_PATH = "/api/v1/orders";

    @Autowired
    private RestUtils restUtils;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Transactional
    public ResponseEntity<Order> createOrder(@Valid @RequestBody final Order order) {
        // 1. check billTo: if its ID is null that means a client is now aware of an already created account.
        // In this case we first can look up by the idempotency key and if not found then create an account.
        //
        // Ultimately  what we need from the account is just an correct account ID which we will use to attach the order
        if (order.getBillTo().getId() == null) {
            final Account billTo = accountRepository.findByIdempotencyKey(order.getBillTo().getIdempotencyKey()).orElseGet(
                    () -> createAccount(order.getBillTo()));
            order.setBillTo(billTo);
        }

        // 2. check address ID is for billing address, if it is null then a client is now aware if an address was already created.
        // In this case we first can look up by the idempotency key and if not found then create an address.
        //
        // Ultimately  what we need from the address is just an correct address ID which we will use for the order
        if (order.getBillingAddress().getId() == null) {
            final Address billingAddress = addressRepository.findByIdempotencyKey(order.getBillingAddress().getIdempotencyKey()).orElseGet(
                    () -> createAddress(order.getBillingAddress()));
            order.setBillingAddress(billingAddress);
        }

        // 3. do the same with shipping address (but it is optional)
        if (order.getShippingAddress() != null && order.getShippingAddress().getId() == null) {
            final Address billingAddress = addressRepository.findByIdempotencyKey(order.getShippingAddress().getIdempotencyKey()).orElseGet(
                    () -> createAddress(order.getBillingAddress()));
            order.setShippingAddress(billingAddress);
        }

        // 4. create an order
        final Order createdOrder;
        try {
            createdOrder = orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            if (!order.isIdempotencyKeyConstraint(e)) {
                throw e;
            }

            final var id = orderRepository.findByIdempotencyKey(order.getIdempotencyKey())
                    .map(t -> t.getId())
                    .orElseThrow(IllegalStateException::new);

            throw new IdempotencyKeyException(OrderController.RESOURCE_PATH, id, e);
        }

        //Send location in response
        return ResponseEntity
                .created(restUtils.getResourceLocation(OrderController.RESOURCE_PATH, createdOrder.getId()))
                .body(order);
    }

    private Account createAccount(final Account billTo) {
        try {
            return accountRepository.save(billTo);
        } catch (DataIntegrityViolationException e) {
            if (!billTo.isIdempotencyKeyConstraint(e)) {
                throw e;
            }

            return accountRepository.findByIdempotencyKey(billTo.getIdempotencyKey()).orElseThrow(
                    () -> new IllegalStateException("a prohibited operation: account was deleted"));
        }
    }

    private Address createAddress(final Address address) {
        try {
            return addressRepository.save(address);
        } catch (DataIntegrityViolationException e) {
            if (!address.isIdempotencyKeyConstraint(e)) {
                throw e;
            }

            return addressRepository.findByIdempotencyKey(address.getIdempotencyKey()).orElseThrow(
                    () -> new IllegalStateException("a prohibited operation: account was deleted"));
        }
    }
}
