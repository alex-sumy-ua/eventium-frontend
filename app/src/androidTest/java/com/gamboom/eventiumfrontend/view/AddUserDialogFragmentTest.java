package com.gamboom.eventiumfrontend.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
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
public class AddUserDialogFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class)
                    .putExtra("TEST_MODE", true));

    @Test
    public void dialogFields_areDisplayed_whenOpened() {
        // Open UserFragment
        onView(withId(R.id.nav_users)).perform(click());
        // Open dialog
        onView(withId(R.id.fab_add)).perform(click());
        // Check input fields are visible
        onView(withId(R.id.et_username)).check(matches(isDisplayed()));
        onView(withId(R.id.et_email)).check(matches(isDisplayed()));
        onView(withId(R.id.spinner_role)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_save)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_cancel)).check(matches(isDisplayed()));
    }

    @Test
    public void cancelButton_closesDialog() {
        onView(withId(R.id.nav_users)).perform(click());
        onView(withId(R.id.fab_add)).perform(click());
        onView(withId(R.id.btn_cancel)).perform(click());
        // After dismissing, username input should no longer be visible
        onView(withId(R.id.et_username)).check(doesNotExist());
    }
}
