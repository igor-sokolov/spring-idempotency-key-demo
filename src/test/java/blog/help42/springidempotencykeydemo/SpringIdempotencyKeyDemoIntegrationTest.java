package blog.help42.springidempotencykeydemo;

import blog.help42.springidempotencykeydemo.exception.ErrorInfo;
import blog.help42.springidempotencykeydemo.model.order.Account;
import blog.help42.springidempotencykeydemo.model.order.Address;
import blog.help42.springidempotencykeydemo.model.order.Order;
import blog.help42.springidempotencykeydemo.model.order.OrderEntry;
import blog.help42.springidempotencykeydemo.model.payment.PaymentTransaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringIdempotencyKeyDemoApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringIdempotencyKeyDemoIntegrationTest {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void retryPostPaymentTransaction() {
		PaymentTransaction t = new PaymentTransaction(Currency.getInstance("USD"), BigDecimal.valueOf(10.99), UUID.randomUUID());

		// Check we have successively post a transaction
		var entity = restTemplate.postForEntity(getUri("/txs/"), t, PaymentTransaction.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(entity.getBody()).isEqualToIgnoringGivenFields(t, "id");

		// post again
		var errorEntity = restTemplate.postForEntity(getUri("/txs/"), t, ErrorInfo.class);
		assertThat(errorEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(errorEntity.getBody()).hasFieldOrPropertyWithValue("message", "Idempotency key violation");
	}

	@Test
	public void retryPostOrder() {
		OrderEntry entry0 = new OrderEntry();
		entry0.setIndex(0);
		entry0.setProductId("239238");
		entry0.setQuantity(1);
		entry0.setPrice(BigDecimal.valueOf(2.99));

		OrderEntry entry1 = new OrderEntry();
		entry1.setIndex(1);
		entry1.setProductId("343535");
		entry1.setQuantity(2);
		entry1.setPrice(BigDecimal.valueOf(4.99));

		Address billingAddress = new Address();
		billingAddress.setLine1("123 Main Street");
		billingAddress.setCity("Philadelphia");
		billingAddress.setRegion("US-PA");
		billingAddress.setZipcode("19104");
		billingAddress.setCountry("US");
		billingAddress.setIdempotencyKey(UUID.randomUUID());

		Account account = new Account();
		account.setName("ACME");
		account.setIdempotencyKey(UUID.randomUUID());

		Order order = new Order();
		order.setBillTo(account);
		order.setEntries(Arrays.asList(entry0, entry1));
		order.setBillingAddress(billingAddress);
		order.setSubtotal(BigDecimal.valueOf(12.97));
		order.setTotalTax(BigDecimal.valueOf(0.78));
		order.setTotal(BigDecimal.valueOf(13.75));
		order.setIdempotencyKey(UUID.randomUUID());

		var entity = restTemplate.postForEntity(getUri("/orders/"), order, Order.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(entity.getBody()).isEqualToIgnoringGivenFields(order, "id");
	}

	private String getUri(String resource) {
		return "http://localhost:" + port + "/api/v1" + resource;
	}
}
