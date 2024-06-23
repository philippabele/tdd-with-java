package com.todoapp.testForDokumentation;

import com.todoapp.controller.UserController;
import com.todoapp.model.UserRegistrationRequest;
import com.todoapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testRegisterUserPasswordMismatch() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password1", "password2");

        BindingResult result = mock(BindingResult.class);
        ResponseEntity<?> response = userController.registerUser(request, result);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password and confirm password do not match", response.getBody());
    }

}
