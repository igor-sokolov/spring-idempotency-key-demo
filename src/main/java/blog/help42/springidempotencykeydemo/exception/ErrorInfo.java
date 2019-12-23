package blog.help42.springidempotencykeydemo.exception;

import org.jetbrains.annotations.Contract;

public class ErrorInfo {
    private String message;

    @Contract(pure = true)
    public ErrorInfo() {

    }

    @Contract(pure = true)
    public ErrorInfo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}