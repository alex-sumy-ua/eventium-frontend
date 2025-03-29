package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.service.RegistrationApiService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RegistrationRepositoryTest {

    @Mock
    private RegistrationApiService mockApiService;

    @Mock
    private Call<Registration> mockRegistrationCall;

    @Mock
    private Call<List<Registration>> mockRegistrationListCall;

    @Mock
    private Call<Void> mockVoidCall;

    private RegistrationRepository registrationRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Manually inject mocked ApiService into repository
        registrationRepository = new RegistrationRepository() {
            {
                // Override authToken and ApiService manually
                try {
                    java.lang.reflect.Field field = RegistrationRepository.class.getDeclaredField("registrationApiService");
                    field.setAccessible(true);
                    field.set(this, mockApiService);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    public void getAllRegistrations_returnsExpectedCall() {
        when(mockApiService.getAllRegistrations(anyString())).thenReturn(mockRegistrationListCall);

        Call<List<Registration>> result = registrationRepository.getAllRegistrations();
        assertEquals(mockRegistrationListCall, result);
        verify(mockApiService).getAllRegistrations(startsWith("Bearer "));
    }

    @Test
    public void getRegistrationById_returnsExpectedCall() {
        UUID id = UUID.randomUUID();
        when(mockApiService.getRegistrationById(anyString(), eq(id))).thenReturn(mockRegistrationCall);

        Call<Registration> result = registrationRepository.getRegistrationById(id);
        assertEquals(mockRegistrationCall, result);
        verify(mockApiService).getRegistrationById(startsWith("Bearer "), eq(id));
    }

    @Test
    public void createRegistration_returnsExpectedCall() {
        Registration registration = new Registration(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        when(mockApiService.createRegistration(anyString(), eq(registration))).thenReturn(mockRegistrationCall);

        Call<Registration> result = registrationRepository.createRegistration(registration);
        assertEquals(mockRegistrationCall, result);
        verify(mockApiService).createRegistration(startsWith("Bearer "), eq(registration));
    }

    @Test
    public void updateRegistration_returnsExpectedCall() {
        UUID id = UUID.randomUUID();
        Registration registration = new Registration(id, UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        when(mockApiService.updateRegistration(anyString(), eq(id), eq(registration))).thenReturn(mockRegistrationCall);

        Call<Registration> result = registrationRepository.updateRegistration(id, registration);
        assertEquals(mockRegistrationCall, result);
        verify(mockApiService).updateRegistration(startsWith("Bearer "), eq(id), eq(registration));
    }

    @Test
    public void deleteRegistration_returnsExpectedCall() {
        UUID id = UUID.randomUUID();
        when(mockApiService.deleteRegistration(anyString(), eq(id))).thenReturn(mockVoidCall);

        Call<Void> result = registrationRepository.deleteRegistration(id);
        assertEquals(mockVoidCall, result);
        verify(mockApiService).deleteRegistration(startsWith("Bearer "), eq(id));
    }
}
