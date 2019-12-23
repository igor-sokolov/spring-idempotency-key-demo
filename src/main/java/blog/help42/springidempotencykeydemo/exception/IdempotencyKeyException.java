package blog.help42.springidempotencykeydemo.exception;

public class IdempotencyKeyException extends RuntimeException {
    public IdempotencyKeyException(RuntimeException e) {
        super(e);
    }
}
