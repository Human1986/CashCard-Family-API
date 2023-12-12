package example.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Just as if we’re on a real project, let’s use test driven development to implement our first API endpoint.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //This will start our Spring Boot application and make it available for our test to perform requests to it.
public class CashCardApplicationTests {
    @Autowired //We've asked Spring to inject a test helper that’ll allow us to make HTTP requests to the locally running application.
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcards/99", String.class); //Here we use restTemplate to make an HTTP GET request to our application endpoint /cashcards/99

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);  //We can inspect many aspects of the response, including the HTTP Response Status code, which we expect to be 200 OK.
    }
}
