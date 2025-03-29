package com.gamboom.eventiumfrontend.service;

import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.repository.RegistrationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RegistrationApiServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createRegistration() {
        Registration registration = new Registration(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        Call<Registration> mockCall = mock(Call.class);
        when(registrationRepository.createRegistration(registration)).thenReturn(mockCall);

        Call<Registration> result = registrationRepository.createRegistration(registration);
        assertNotNull(result);
        verify(registrationRepository, times(1)).createRegistration(registration);
    }

    @Test
    public void getAllRegistrations() {
        Call<java.util.List<Registration>> mockCall = mock(Call.class);
        when(registrationRepository.getAllRegistrations()).thenReturn(mockCall);

        Call<java.util.List<Registration>> result = registrationRepository.getAllRegistrations();
        assertNotNull(result);
        verify(registrationRepository, times(1)).getAllRegistrations();
    }

    @Test
    public void getRegistrationById() {
        UUID id = UUID.randomUUID();
        Call<Registration> mockCall = mock(Call.class);
        when(registrationRepository.getRegistrationById(id)).thenReturn(mockCall);

        Call<Registration> result = registrationRepository.getRegistrationById(id);
        assertNotNull(result);
        verify(registrationRepository, times(1)).getRegistrationById(id);
    }

    @Test
    public void updateRegistration() {
        UUID id = UUID.randomUUID();
        Registration updated = new Registration(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        Call<Registration> mockCall = mock(Call.class);
        when(registrationRepository.updateRegistration(id, updated)).thenReturn(mockCall);

        Call<Registration> result = registrationRepository.updateRegistration(id, updated);
        assertNotNull(result);
        verify(registrationRepository, times(1)).updateRegistration(id, updated);
    }

    @Test
    public void deleteRegistration() {
        UUID id = UUID.randomUUID();
        Call<Void> mockCall = mock(Call.class);
        when(registrationRepository.deleteRegistration(id)).thenReturn(mockCall);

        Call<Void> result = registrationRepository.deleteRegistration(id);
        assertNotNull(result);
        verify(registrationRepository, times(1)).deleteRegistration(id);
    }
}
