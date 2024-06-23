package com.todoapp.testForDokumentation;

import com.todoapp.model.UserRegistrationRequest;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRegistrationAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRegisterUser() {
        String url = "http://localhost:" + port + "/register";
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", "password");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<UserRegistrationRequest> entity = new HttpEntity<>(request, headers);

        // Send HTTP POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Check if the response status code is 409 (CONFLICT)
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

}
