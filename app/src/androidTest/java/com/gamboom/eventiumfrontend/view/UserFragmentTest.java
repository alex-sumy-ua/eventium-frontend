package com.gamboom.eventiumfrontend.view;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
public class UserFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class)
                    .putExtra("TEST_MODE", true));

    @Test
    public void userRecyclerView_isDisplayed() {
        onView(withId(R.id.nav_users)).perform(click());
        onView(withId(R.id.userRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void fabAdd_isVisible() {
        // Step 1: Navigate to UserFragment
        onView(withId(R.id.nav_users)).perform(click());
        // Step 2: Check if the FAB is shown (assuming test user is STAFF or ADMIN)
        onView(withId(R.id.fab_add)).check(matches(isDisplayed()));
    }

    @Test
    public void clickingFabAdd_opensAddUserDialog_whenAuthorized() {
        // Step 1: Tap on the bottom navigation item for users
        onView(withId(R.id.nav_users)).perform(click());
        // Step 2: Now fab_add should be visible in UserFragment
        onView(withId(R.id.fab_add)).perform(click());
        // Step 3: Check that the dialog is shown
        onView(withId(R.id.et_username)).check(matches(isDisplayed()));
    }

    @Test
    public void unauthorizedUser_cannotEditOrDeleteUsers() {
        // Step 1: Navigate to UserFragment
        onView(withId(R.id.nav_users)).perform(click());
        // Step 2: Check that edit/delete buttons do NOT exist for MEMBER
        onView(withId(R.id.btnEdit)).check(doesNotExist());
        onView(withId(R.id.btnDelete)).check(doesNotExist());
    }

}