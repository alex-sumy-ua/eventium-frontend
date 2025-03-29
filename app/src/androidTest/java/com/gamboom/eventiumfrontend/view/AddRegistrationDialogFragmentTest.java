package com.gamboom.eventiumfrontend.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.not;

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
public class AddRegistrationDialogFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class)
                    .putExtra("TEST_MODE", true));

    @Test
    public void addRegistrationDialog_fieldsAreVisible_whenEventsExist_orSkipTest() throws InterruptedException {
        onView(withId(R.id.nav_event_registrations)).perform(click());
        onView(withId(R.id.fab_add)).perform(click());

        Thread.sleep(2000); // Still needed without IdlingResource

        // If noEventsMessage is shown, skip test
        try {
            onView(withId(R.id.noEventsMessage)).check(matches(isDisplayed()));
            System.out.println("Skipping test: No future events found.");
            return; // Skip the rest of the test
        } catch (AssertionError ignored) {
            // Continue if no message — means future events exist
        }

        // Check normal fields
        onView(withId(R.id.eventSpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.eventTime)).check(matches(isDisplayed()));
        onView(withId(R.id.eventLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_save)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_cancel)).check(matches(isDisplayed()));
    }


    @Test
    public void addRegistrationDialog_showsNoEventsMessage_whenNoFutureEventsExist() throws InterruptedException {
        onView(withId(R.id.nav_event_registrations)).perform(click());
        onView(withId(R.id.fab_add)).perform(click());

        Thread.sleep(2000);

        onView(withId(R.id.noEventsMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.eventSpinner)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btn_save)).check(matches(not(isDisplayed())));
    }

    @Test
    public void addRegistrationDialog_cancelButton_closesDialog() throws InterruptedException {
        onView(withId(R.id.nav_event_registrations)).perform(click());
        onView(withId(R.id.fab_add)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.btn_cancel)).perform(scrollTo(), click());
        onView(withId(R.id.btn_cancel)).check(doesNotExist());
    }

}
