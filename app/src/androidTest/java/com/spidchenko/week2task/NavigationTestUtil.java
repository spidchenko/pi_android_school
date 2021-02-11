package com.spidchenko.week2task;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class NavigationTestUtil {

    public static final int NAV_SEARCH = 1;
    public static final int NAV_FAVOURITES = 2;
    public static final int NAV_GALLERY = 3;
    public static final int NAV_SEARCH_HISTORY = 4;
    public static final int NAV_MAPS = 5;

    public static void navigateTo(int destination) {
        switch (destination) {
            case NAV_SEARCH:
                break;
            case NAV_FAVOURITES:
                openNavigationDrawer();
                onView(withId(R.id.favouritesFragment)).perform(click());
                break;
            case NAV_GALLERY:
                openNavigationDrawer();
                onView(withId(R.id.galleryFragment)).perform(click());
                break;
            case NAV_SEARCH_HISTORY:
                openNavigationDrawer();
                onView(withId(R.id.searchHistoryFragment)).perform(click());
                break;
            case NAV_MAPS:
                openNavigationDrawer();
                onView(withId(R.id.mapsFragment)).perform(click());
                break;
        }
    }

    public static void openNavigationDrawer() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
    }
}
