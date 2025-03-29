package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.service.EventApiService;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EventRepositoryTest {

    private EventRepository eventRepository;
    private EventApiService mockApiService;
    private final String token = "Bearer test-token";

    @Before
    public void setUp() {
        mockApiService = mock(EventApiService.class);

        eventRepository = new EventRepository() {
            {
                try {
                    java.lang.reflect.Field field = EventRepository.class.getDeclaredField("eventApiService");
                    field.setAccessible(true);
                    field.set(this, mockApiService);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Call<List<Event>> getAllEvents() {
                return mockApiService.getAllEvents(token);
            }

            @Override
            public Call<Event> createEvent(Event event) {
                return mockApiService.createEvent(token, event);
            }

            @Override
            public Call<Event> getEventById(UUID id) {
                return mockApiService.getEventById(token, id);
            }

            @Override
            public Call<Event> updateEvent(UUID id, Event event) {
                return mockApiService.updateEvent(token, id, event);
            }

            @Override
            public Call<Void> deleteEvent(UUID id) {
                return mockApiService.deleteEvent(token, id);
            }
        };
    }

    @Test
    public void getAllEvents_shouldReturnCall() {
        Call<List<Event>> mockCall = mock(Call.class);
        when(mockApiService.getAllEvents(token)).thenReturn(mockCall);

        Call<List<Event>> result = eventRepository.getAllEvents();
        assertNotNull(result);
        verify(mockApiService).getAllEvents(token);
    }

    @Test
    public void createEvent_shouldReturnCall() {
        Event event = new Event(UUID.randomUUID(), "Title", "Desc", "Place",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                UUID.randomUUID(), LocalDateTime.now());

        Call<Event> mockCall = mock(Call.class);
        when(mockApiService.createEvent(token, event)).thenReturn(mockCall);

        Call<Event> result = eventRepository.createEvent(event);
        assertNotNull(result);
        verify(mockApiService).createEvent(token, event);
    }

    @Test
    public void getEventById_shouldReturnCall() {
        UUID id = UUID.randomUUID();
        Call<Event> mockCall = mock(Call.class);
        when(mockApiService.getEventById(token, id)).thenReturn(mockCall);

        Call<Event> result = eventRepository.getEventById(id);
        assertNotNull(result);
        verify(mockApiService).getEventById(token, id);
    }

    @Test
    public void updateEvent_shouldReturnCall() {
        UUID id = UUID.randomUUID();
        Event event = new Event(id, "Updated", "Updated", "Here",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                UUID.randomUUID(), LocalDateTime.now());

        Call<Event> mockCall = mock(Call.class);
        when(mockApiService.updateEvent(token, id, event)).thenReturn(mockCall);

        Call<Event> result = eventRepository.updateEvent(id, event);
        assertNotNull(result);
        verify(mockApiService).updateEvent(token, id, event);
    }

    @Test
    public void deleteEvent_shouldReturnCall() {
        UUID id = UUID.randomUUID();
        Call<Void> mockCall = mock(Call.class);
        when(mockApiService.deleteEvent(token, id)).thenReturn(mockCall);

        Call<Void> result = eventRepository.deleteEvent(id);
        assertNotNull(result);
        verify(mockApiService).deleteEvent(token, id);
    }
}
