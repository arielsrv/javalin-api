package com.iskaypet.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.iskaypet.dto.UserDTO;
import com.iskaypet.services.UserService;
import io.javalin.http.Context;
import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserControllerTest {

    @Mock
    UserService userService;
    @Mock
    Context context;
    @InjectMocks
    UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers_returnsUserDTOList() {
        UserDTO user1 = new UserDTO();
        user1.userId = 1L;
        user1.name = "Alice";
        user1.email = "alice@example.com";
        UserDTO user2 = new UserDTO();
        user2.userId = 2L;
        user2.name = "Bob";
        user2.email = "bob@example.com";
        List<UserDTO> userList = Arrays.asList(user1, user2);
        when(userService.getUsers()).thenReturn(Observable.just(userList));

        List<UserDTO> result = userController.getUsers(context).blockingFirst();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).userId).isEqualTo(1L);
        assertThat(result.get(0).name).isEqualTo("Alice");
        assertThat(result.get(0).email).isEqualTo("alice@example.com");
        assertThat(result.get(1).userId).isEqualTo(2L);
        assertThat(result.get(1).name).isEqualTo("Bob");
        assertThat(result.get(1).email).isEqualTo("bob@example.com");
    }
}
