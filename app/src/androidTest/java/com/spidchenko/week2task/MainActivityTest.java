package com.spidchenko.week2task;

import android.content.Context;
import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.spidchenko.week2task.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Login Fragment:

    @Test
    public void loginOnEmptyUsername_errorMessageVisible() {
        onView(withId(R.id.btn_sign_in)).perform(click());
        onView(withId(R.id.username_input_layout)).check(matches(hasDescendant(
                withText(appContext.getString(R.string.login_failed)))));
    }

    @Test
    public void loginWithProperUsername_SearchFragmentVisible() {
        onView(withId(R.id.username)).perform(typeText("123"), pressImeActionButton());
        onView(withId(R.id.btn_sign_in)).perform(click());
        onView(withId(R.id.rv_images)).check(matches(isDisplayed()));
    }

    // Search Fragment

    @Test
    public void searchPicsWithEmptyRequest_errorMessageVisible() {
        loginWithProperUsername_SearchFragmentVisible();
        onView(withId(R.id.btn_search)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.error_empty_search)));
    }

    @Test
    public void testDrawer_visibleWhenOpen() {
        loginWithProperUsername_SearchFragmentVisible();
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(ViewMatchers.withId(R.id.nav_search)).check(matches(isDisplayed()));
    }
}