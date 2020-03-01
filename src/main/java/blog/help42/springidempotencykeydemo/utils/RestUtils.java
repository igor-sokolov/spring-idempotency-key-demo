package blog.help42.springidempotencykeydemo.utils;

import blog.help42.springidempotencykeydemo.controller.PaymentTransactionController;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Component
public class RestUtils {
    public URI getResourceLocation(String resourcePath, Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path(PaymentTransactionController.RESOURCE_PATH)
                .buildAndExpand(id)
                .toUri();
    }
}
