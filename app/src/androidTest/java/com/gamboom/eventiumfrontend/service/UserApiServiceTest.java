package com.gamboom.eventiumfrontend.service;

import com.gamboom.eventiumfrontend.model.Role;
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserApiServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserApiServiceTest userApiServiceTest; // Not directly used but needed for Mockito setup

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUser() {
        User user = new User(
                UUID.randomUUID(),
                "Alex",
                "alex@example.com",
                "password",
                Role.STAFF,
                LocalDateTime.now()
        );

        Call<User> mockCall = mock(Call.class);
        when(userRepository.createUser(user)).thenReturn(mockCall);

        Call<User> result = userRepository.createUser(user);
        assertNotNull(result);
        verify(userRepository, times(1)).createUser(user);
    }

    @Test
    public void getAllUsers() {
        Call<List<User>> mockCall = mock(Call.class);
        when(userRepository.getAllUsers()).thenReturn(mockCall);

        Call<List<User>> result = userRepository.getAllUsers();
        assertNotNull(result);
        verify(userRepository, times(1)).getAllUsers();
    }

    @Test
    public void getUserById() {
        UUID id = UUID.randomUUID();
        Call<User> mockCall = mock(Call.class);
        when(userRepository.getUserById(id)).thenReturn(mockCall);

        Call<User> result = userRepository.getUserById(id);
        assertNotNull(result);
        verify(userRepository, times(1)).getUserById(id);
    }

    @Test
    public void getUserByEmail() {
        String email = "staff@example.com";
        Call<User> mockCall = mock(Call.class);
        when(userRepository.getUserByEmail(email)).thenReturn(mockCall);

        Call<User> result = userRepository.getUserByEmail(email);
        assertNotNull(result);
        verify(userRepository, times(1)).getUserByEmail(email);
    }

    @Test
    public void updateUser() {
        UUID id = UUID.randomUUID();
        User updated = new User(
                id,
                "Updated Name",
                "updated@example.com",
                "newpass",
                Role.STAFF,
                LocalDateTime.now()
        );

        Call<User> mockCall = mock(Call.class);
        when(userRepository.updateUser(id, updated)).thenReturn(mockCall);

        Call<User> result = userRepository.updateUser(id, updated);
        assertNotNull(result);
        verify(userRepository, times(1)).updateUser(id, updated);
    }

    @Test
    public void deleteUser() {
        UUID id = UUID.randomUUID();
        Call<Void> mockCall = mock(Call.class);
        when(userRepository.deleteUser(id)).thenReturn(mockCall);

        Call<Void> result = userRepository.deleteUser(id);
        assertNotNull(result);
        verify(userRepository, times(1)).deleteUser(id);
    }
}
