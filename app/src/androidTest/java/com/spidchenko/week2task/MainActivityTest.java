package com.spidchenko.week2task;

import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.spidchenko.week2task.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    private static final String TEST_USERNAME = "User For Testing";
    private static final String TEST_SEARCH = "Dogs";
    private static final String TEST_SEARCH_NO_RESULTS = "Fsf353sdP#f";

    private static final int NAV_SEARCH = 1;
    private static final int NAV_FAVOURITES = 2;
    private static final int NAV_GALLERY = 3;
    private static final int NAV_SEARCH_HISTORY = 4;
    private static final int NAV_MAPS = 5;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

//    @Rule
//    public IntentsTestRule<MainActivity> intentsTestRule =
//            new IntentsTestRule<>(MainActivity.class);


    @Before
    public void setUp() {
        // Log in as user
        onView(withId(R.id.username)).perform(typeText(TEST_USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.btn_sign_in)).perform(click());
    }


    // Login Fragment Tests:


    @Test
    public void loginWithProperUsername_searchFragmentVisible() {
        onView(withId(R.id.rv_images)).check(matches(isDisplayed()));
    }


    // Search Fragment Tests:


    @Test
    public void searchPicsWithEmptyRequest_errorMessageVisible() {
        onView(withId(R.id.et_search_query)).perform(clearText());
        onView(withId(R.id.btn_search)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.error_empty_search)));
    }

    @Test
    public void searchPicsWithUnknownText_nothingFoundMessageVisible() throws InterruptedException {
        onView(withId(R.id.et_search_query))
                .perform(clearText())
                .perform(typeText(TEST_SEARCH_NO_RESULTS));

        onView(withId(R.id.btn_search)).perform(click());
        Thread.sleep(1000);
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.error_nothing_found)));
    }

    @Test
    public void testDrawer_visibleWhenOpen() {
        openNavigationDrawer();
        onView(withId(R.id.nav_search)).check(matches(isDisplayed()));
    }

    @Test
    public void searchPictures_progressBarVisible() {
        onView(withId(R.id.et_search_query))
                .perform(clearText())
                .perform(typeText(TEST_SEARCH));
        closeSoftKeyboard();
        onView(withId(R.id.btn_search)).perform(click());
        onView(withId(R.id.pbLoading)).check(matches(isDisplayed()));
    }

    @Test
    public void searchPictures_somePicturesLoaded() throws InterruptedException {
        onView(withId(R.id.et_search_query))
                .perform(clearText())
                .perform(typeText(TEST_SEARCH), pressImeActionButton());
        Thread.sleep(2000);

        // Some pictures loaded
        onView(withId(R.id.rv_images)).check(matches(hasDescendant(withText(TEST_SEARCH))));
    }

    @Test
    public void searchAndScroll_Scrolled() throws InterruptedException {
        onView(withId(R.id.et_search_query))
                .perform(clearText())
                .perform(typeText(TEST_SEARCH), pressImeActionButton());
        Thread.sleep(2000);

        onView(withId(R.id.rv_images))
                .perform(scrollToPosition(28))
                .check(matches(hasDescendant(withText(TEST_SEARCH))));
    }


    // Image Viewer Fragment Test


    @Test
    public void validSearchAndClickOnResultPicture_ImageViewerFragmentVisible() throws InterruptedException {
        onView(withId(R.id.et_search_query))
                .perform(clearText())
                .perform(typeText(TEST_SEARCH));
        closeSoftKeyboard();
        onView(withId(R.id.btn_search)).perform(click());
        Thread.sleep(2000);


        onView(withId(R.id.rv_images))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check if ImageViewerFragment opened
        onView(withId(R.id.webView)).check(matches(isDisplayed()));
        // Like
        onView(withId(R.id.cb_toggle_favourite)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(containsString(" Favourites"))));
        // Save
        onView(withId(R.id.img_save)).perform(click());
    }


    // Gallery Fragment Test

    @Test
    public void test() throws InterruptedException {

        navigate_to(NAV_GALLERY);
        onView(withId(R.id.btn_make_photo)).perform(click());
        // Check Camera Fragment Visible
        onView(withId(R.id.previewView)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_take_shot)).perform(click());

        Espresso.pressBack();
        Espresso.pressBack();
        Thread.sleep(10000);

        //TODO

    }


    // Navigation Tests:


    @Test
    public void navigateToFavouritesScreen_favouritesFragmentVisible() {
        navigate_to(NAV_FAVOURITES);
        onView(withId(R.id.rv_favourite_images)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToGalleryScreen_galleryFragmentVisible() {
        navigate_to(NAV_GALLERY);
        onView(withId(R.id.rv_gallery_images)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToSearchHistoryScreen_searchHistoryFragmentVisible() {
        navigate_to(NAV_SEARCH_HISTORY);
        onView(withId(R.id.rv_search_history)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToMapsScreen_mapsFragmentVisible() {
        navigate_to(NAV_MAPS);
        onView(withId(R.id.btn_map_search)).check(matches(isDisplayed()));
    }


    private void navigate_to(int destination) {
        switch (destination) {
            case NAV_SEARCH:
                break;
            case NAV_FAVOURITES:
                openNavigationDrawer();
                onView(withId(R.id.nav_favourites)).perform(click());
                break;
            case NAV_GALLERY:
                openNavigationDrawer();
                onView(withId(R.id.nav_gallery)).perform(click());
                break;
            case NAV_SEARCH_HISTORY:
                openNavigationDrawer();
                onView(withId(R.id.nav_search_history)).perform(click());
                break;
            case NAV_MAPS:
                openNavigationDrawer();
                onView(withId(R.id.nav_maps)).perform(click());
                break;
        }
    }

    private void openNavigationDrawer() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
    }
}