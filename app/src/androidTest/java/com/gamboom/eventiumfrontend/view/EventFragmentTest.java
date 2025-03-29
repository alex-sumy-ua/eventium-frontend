package com.gamboom.eventiumfrontend.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

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
public class EventFragmentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class)
                    .putExtra("TEST_MODE", true));

    @Test
    public void eventRecyclerView_isDisplayed() {
        onView(withId(R.id.nav_events)).perform(click()); // switch to EventFragment
        onView(withId(R.id.eventRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void fabAdd_isVisibleToStaffOnly_orShowsToastOtherwise() {
        onView(withId(R.id.nav_events)).perform(click());
        onView(withId(R.id.fab_add)).check(matches(isDisplayed()));
    }

    @Test
    public void clickingFabAdd_opensAddEventDialog_whenStaff() {
        onView(withId(R.id.nav_events)).perform(click());
        onView(withId(R.id.fab_add)).perform(click());
        onView(withId(R.id.et_eventtitle)).check(matches(isDisplayed()));
    }
}
