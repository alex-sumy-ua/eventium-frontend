package com.gamboom.eventiumfrontend.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.service.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegistrationFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class)
                    .putExtra("TEST_MODE", true));

    @Test
    public void registrationRecyclerView_isDisplayed() {
        onView(withId(R.id.nav_event_registrations)).perform(click());
        onView(withId(R.id.registrationRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void fabAdd_opensAddRegistrationDialog() {
        onView(withId(R.id.nav_event_registrations)).perform(click());
        onView(withId(R.id.fab_add)).perform(click());
    }

    @Test
    public void toggleFilterButton_worksCorrectly() {
        onView(withId(R.id.nav_event_registrations)).perform(click());
        onView(withId(R.id.toggle_filter)).perform(click());
    }
}
