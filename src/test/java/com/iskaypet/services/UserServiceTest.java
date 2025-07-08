package com.iskaypet.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.iskaypet.clients.UserClient;
import com.iskaypet.clients.responses.UserResponse;
import com.iskaypet.dto.UserDTO;
import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

    @Mock
    UserClient userClient;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers_mapsUserResponsesToUserDTOs() {
        UserResponse user1 = new UserResponse();
        user1.id = 1L;
        user1.name = "Alice";
        user1.email = "alice@example.com";
        UserResponse user2 = new UserResponse();
        user2.id = 2L;
        user2.name = "Bob";
        user2.email = "bob@example.com";
        List<UserResponse> userResponses = Arrays.asList(user1, user2);
        when(userClient.getUsers()).thenReturn(Observable.just(userResponses));

        List<UserDTO> result = userService.getUsers().blockingFirst();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).userId).isEqualTo(1L);
        assertThat(result.get(0).name).isEqualTo("Alice");
        assertThat(result.get(0).email).isEqualTo("alice@example.com");
        assertThat(result.get(1).userId).isEqualTo(2L);
        assertThat(result.get(1).name).isEqualTo("Bob");
        assertThat(result.get(1).email).isEqualTo("bob@example.com");
    }
}
