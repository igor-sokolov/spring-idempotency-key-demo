package blog.help42.springidempotencykeydemo.controller;


import blog.help42.springidempotencykeydemo.exception.IdempotencyKeyException;
import blog.help42.springidempotencykeydemo.model.payment.PaymentTransaction;
import blog.help42.springidempotencykeydemo.repository.PaymentTransactionRepository;
import blog.help42.springidempotencykeydemo.utils.RestUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping(path = PaymentTransactionController.RESOURCE_PATH)
public class PaymentTransactionController {
    public static final String RESOURCE_PATH = "/api/v1/txs";

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private RestUtils restUtils;

    @GetMapping(produces = "application/json")
    public Page<PaymentTransaction> getPaymentTransactions(Pageable pageable) {
        return paymentTransactionRepository.findAll(pageable);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<PaymentTransaction> createPaymentTransaction(@Valid @RequestBody PaymentTransaction transaction) {
        try {
            transaction = paymentTransactionRepository.save(transaction);
        } catch (DataIntegrityViolationException e) {
            if (!transaction.isIdempotencyKeyConstraint(e)) {
                throw e;
            }

            final var id = paymentTransactionRepository.findByIdempotencyKey(transaction.getIdempotencyKey())
                    .map(t -> t.getId())
                    .orElseThrow(IllegalStateException::new);

            throw new IdempotencyKeyException(OrderController.RESOURCE_PATH, id, e);
        }

        //Send location in response
        return ResponseEntity
                .created(restUtils.getResourceLocation(PaymentTransactionController.RESOURCE_PATH, transaction.getId()))
                .body(transaction);
    }

    @PutMapping(path = "/{txId}", consumes = "application/json", produces = "application/json")
    public PaymentTransaction updatePaymentTransaction(@PathVariable Long txId, @Valid @RequestBody PaymentTransaction paymentTransaction) {
        return paymentTransactionRepository.findById(txId)
                .map(t -> {
                    t.setCurrency(paymentTransaction.getCurrency());
                    t.setAmount(paymentTransaction.getAmount());
                    return paymentTransactionRepository.save(t);
                }).orElseThrow(() -> new NoSuchElementException("Order not found with id = " + txId));
    }

    private RuntimeException handleDataIntegrityViolationException(DataIntegrityViolationException e, UUID idempotenceKey) {
        var cause = e.getCause();
        if (cause instanceof ConstraintViolationException &&
                PaymentTransaction.IDEMPOTENCY_KEY_CONSTRAINT
                        .equals(((ConstraintViolationException) cause).getConstraintName())) {
            Long id = paymentTransactionRepository.findByIdempotencyKey(idempotenceKey)
                    .map(t -> t.getId())
                    .orElseThrow(IllegalStateException::new);

            return new IdempotencyKeyException(PaymentTransactionController.RESOURCE_PATH, id, e);
        }

        return e;
    }
}
