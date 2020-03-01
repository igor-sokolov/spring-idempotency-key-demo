package blog.help42.springidempotencykeydemo.exception;

import lombok.Getter;

@Getter
public class IdempotencyKeyException extends RuntimeException {
    private final Long resourceId;
    private final String resourcePath;

    public IdempotencyKeyException(String resourcePath, Long resourceId, RuntimeException e) {
        super(e);
        this.resourceId = resourceId;
        this.resourcePath = resourcePath;
    }
}
