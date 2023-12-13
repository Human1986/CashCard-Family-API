package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Just as if we’re on a real project, let’s use test driven development to implement our first API endpoint.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//This will start our Spring Boot application and make it available for our test to perform requests to it.
public class CashCardApplicationTests {
    @Autowired //We've asked Spring to inject a test helper that’ll allow us to make HTTP requests to the locally running application.
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards/99", String.class); //Here we use restTemplate to make an HTTP GET request to our application endpoint /cashcards/99
        DocumentContext documentContext = JsonPath.parse(response.getBody());

        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99L);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);  //We can inspect many aspects of the response, including the HTTP Response Status code, which we expect to be 200 OK.
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(44L, 250.00);
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);  //According to the official specification: the origin server SHOULD send a 201 (Created) response ...


        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();  //The official spec continues to state the following: send a 201 (Created) response containing a Location header field that provides an identifier for the primary resource created ...

        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);  // Finally, we'll use the Location header's information to fetch the newly created CashCard.

        /**
         * The additions verify that the new CashCard.id is not null,
         * and the newly created CashCard.amount is 250.00, just as we specified at creation time.
         */

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }
}
