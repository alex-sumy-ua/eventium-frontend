package com.gamboom.eventiumfrontend.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

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
public class AddEventDialogFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class)
                    .putExtra("TEST_MODE", true)); // Logged-in user must be STAFF for this test

    @Test
    public void addEventDialogFields_areDisplayed_whenOpened() {
        // Open EventFragment
        onView(withId(R.id.nav_events)).perform(click());

        // Click FAB to open the dialog
        onView(withId(R.id.fab_add)).perform(click());

        // Check important dialog fields are visible (with scrollTo if needed)
        onView(withId(R.id.et_eventtitle)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.et_eventdescription)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.et_eventlocation)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.date_picker_start)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.time_picker_start)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.date_picker_end)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.time_picker_end)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.btn_save)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.btn_cancel)).perform(scrollTo()).check(matches(isDisplayed()));
    }


    @Test
    public void cancelButton_closesDialog() {
        onView(withId(R.id.nav_events)).perform(click());
        onView(withId(R.id.fab_add)).perform(click());
        onView(withId(R.id.btn_cancel)).perform(scrollTo(), click());
        onView(withId(R.id.et_eventtitle)).check(doesNotExist());
    }
}
