package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Role;
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.service.UserApiService;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private UserApiService mockApiService;
    private UserRepository userRepository;

    // Testable subclass to override auth logic
    static class TestableUserRepository extends UserRepository {
        private final UserApiService injectedApiService;

        public TestableUserRepository(UserApiService apiService) {
            this.injectedApiService = apiService;
        }

        @Override
        public Call<List<User>> getAllUsers() {
            return injectedApiService.getAllUsers("Bearer test-token");
        }

        @Override
        public Call<User> createUser(User user) {
            return injectedApiService.createUser("Bearer test-token", user);
        }

        @Override
        public Call<User> getUserById(UUID id) {
            return injectedApiService.getUserById("Bearer test-token", id);
        }

        @Override
        public Call<User> updateUser(UUID id, User user) {
            return injectedApiService.updateUser("Bearer test-token", id, user);
        }

        @Override
        public Call<Void> deleteUser(UUID id) {
            return injectedApiService.deleteUser("Bearer test-token", id);
        }
    }

    @Before
    public void setUp() {
        mockApiService = mock(UserApiService.class);
        userRepository = new TestableUserRepository(mockApiService);
    }

    @Test
    public void getAllUsers_shouldReturnUserList() {
        Call<List<User>> mockCall = mock(Call.class);
        when(mockApiService.getAllUsers(anyString())).thenReturn(mockCall);

        Call<List<User>> result = userRepository.getAllUsers();
        assertNotNull(result);
        verify(mockApiService).getAllUsers("Bearer test-token");
    }

    @Test
    public void createUser_shouldReturnCall() {
        User user = new User(
                UUID.randomUUID(), "Test", "test@example.com", "password",
                Role.MEMBER, LocalDateTime.now());

        Call<User> mockCall = mock(Call.class);
        when(mockApiService.createUser(anyString(), eq(user))).thenReturn(mockCall);

        Call<User> result = userRepository.createUser(user);
        assertNotNull(result);
        verify(mockApiService).createUser("Bearer test-token", user);
    }

    @Test
    public void getUserById_shouldReturnCall() {
        UUID userId = UUID.randomUUID();
        Call<User> mockCall = mock(Call.class);
        when(mockApiService.getUserById(anyString(), eq(userId))).thenReturn(mockCall);

        Call<User> result = userRepository.getUserById(userId);
        assertNotNull(result);
        verify(mockApiService).getUserById("Bearer test-token", userId);
    }

    @Test
    public void updateUser_shouldReturnCall() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Updated", "up@example.com", "pass",
                Role.STAFF, LocalDateTime.now());

        Call<User> mockCall = mock(Call.class);
        when(mockApiService.updateUser(anyString(), eq(userId), eq(user))).thenReturn(mockCall);

        Call<User> result = userRepository.updateUser(userId, user);
        assertNotNull(result);
        verify(mockApiService).updateUser("Bearer test-token", userId, user);
    }

    @Test
    public void deleteUser_shouldReturnCall() {
        UUID userId = UUID.randomUUID();
        Call<Void> mockCall = mock(Call.class);
        when(mockApiService.deleteUser(anyString(), eq(userId))).thenReturn(mockCall);

        Call<Void> result = userRepository.deleteUser(userId);
        assertNotNull(result);
        verify(mockApiService).deleteUser("Bearer test-token", userId);
    }
}
