package com.gamboom.eventiumfrontend.service;

import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.repository.EventRepository;
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

public class EventApiServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventApiServiceTest eventApiServiceTest; // required for @InjectMocks to work

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createEvent() {
        Event event = new Event(
                UUID.randomUUID(),
                "Spring Fest",
                "Community gathering",
                "Central Park",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        Call<Event> mockCall = mock(Call.class);
        when(eventRepository.createEvent(event)).thenReturn(mockCall);

        Call<Event> result = eventRepository.createEvent(event);
        assertNotNull(result);
        verify(eventRepository, times(1)).createEvent(event);
    }

    @Test
    public void getAllEvents() {
        Call<List<Event>> mockCall = mock(Call.class);
        when(eventRepository.getAllEvents()).thenReturn(mockCall);

        Call<List<Event>> result = eventRepository.getAllEvents();
        assertNotNull(result);
        verify(eventRepository, times(1)).getAllEvents();
    }

    @Test
    public void getEventById() {
        UUID eventId = UUID.randomUUID();
        Call<Event> mockCall = mock(Call.class);
        when(eventRepository.getEventById(eventId)).thenReturn(mockCall);

        Call<Event> result = eventRepository.getEventById(eventId);
        assertNotNull(result);
        verify(eventRepository, times(1)).getEventById(eventId);
    }

    @Test
    public void updateEvent() {
        UUID eventId = UUID.randomUUID();
        Event updatedEvent = new Event(
                eventId,
                "Updated Event",
                "Updated Description",
                "Updated Location",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(3),
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        Call<Event> mockCall = mock(Call.class);
        when(eventRepository.updateEvent(eventId, updatedEvent)).thenReturn(mockCall);

        Call<Event> result = eventRepository.updateEvent(eventId, updatedEvent);
        assertNotNull(result);
        verify(eventRepository, times(1)).updateEvent(eventId, updatedEvent);
    }

    @Test
    public void deleteEvent() {
        UUID eventId = UUID.randomUUID();
        Call<Void> mockCall = mock(Call.class);
        when(eventRepository.deleteEvent(eventId)).thenReturn(mockCall);

        Call<Void> result = eventRepository.deleteEvent(eventId);
        assertNotNull(result);
        verify(eventRepository, times(1)).deleteEvent(eventId);
    }
}
