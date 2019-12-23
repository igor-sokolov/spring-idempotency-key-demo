package blog.help42.springidempotencykeydemo.controller;

import blog.help42.springidempotencykeydemo.exception.ErrorInfo;
import blog.help42.springidempotencykeydemo.exception.IdempotencyKeyException;
import blog.help42.springidempotencykeydemo.model.payment.PaymentTransaction;
import blog.help42.springidempotencykeydemo.repository.PaymentTransactionRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/txs")
public class PaymentTransactionController {
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @GetMapping(produces = "application/json")
    public Page<PaymentTransaction> getPaymentTransactions(Pageable pageable) {
        return paymentTransactionRepository.findAll(pageable);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<PaymentTransaction> createPaymentTransaction(@Valid @RequestBody PaymentTransaction transaction) {
        try {
            transaction = paymentTransactionRepository.save(transaction);
        } catch (DataIntegrityViolationException e) {
            throw handleDataIntegrityViolationException(e, transaction.getIdempotencyKey());
        }

        //Create resource location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(transaction.getId())
                .toUri();

        //Send location in response
        return ResponseEntity.created(location).body(transaction);
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

    @ExceptionHandler(IdempotencyKeyException.class)
    public ResponseEntity<ErrorInfo> handleIdempotencyKeyException(DataIntegrityViolationException e) {
        return new ResponseEntity(new ErrorInfo("Idempotency key violation"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity(new ErrorInfo("Data integrity error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private RuntimeException handleDataIntegrityViolationException(DataIntegrityViolationException e, UUID idempotenceKey) {
        var cause = e.getCause();
        if (cause instanceof ConstraintViolationException &&
                PaymentTransaction.IDEMPOTENCY_KEY_CONSTRAINT
                        .equals(((ConstraintViolationException) cause).getConstraintName())) {
            Long txId = paymentTransactionRepository.findByIdempotencyKey(idempotenceKey)
                    .map(t -> t.getId())
                    .orElseThrow(IllegalStateException::new);

            return new IdempotencyKeyException(e);
        }

        return e;
    }
}
