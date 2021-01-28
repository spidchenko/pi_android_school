package com.spidchenko.week2task;

import androidx.test.espresso.Espresso;
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
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.spidchenko.week2task.NavigationTestUtil.NAV_FAVOURITES;
import static com.spidchenko.week2task.NavigationTestUtil.NAV_GALLERY;
import static com.spidchenko.week2task.NavigationTestUtil.NAV_MAPS;
import static com.spidchenko.week2task.NavigationTestUtil.NAV_SEARCH_HISTORY;
import static com.spidchenko.week2task.NavigationTestUtil.navigate_to;
import static com.spidchenko.week2task.NavigationTestUtil.openNavigationDrawer;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    private static final String TEST_USERNAME = "User For Testing";
    private static final String TEST_SEARCH = "Dogs";
    private static final String TEST_SEARCH_NO_RESULTS = "Fsf353sdP#f";

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

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
    public void searchPicsWithEmptyRequest_errorMessage() {
        onView(withId(R.id.et_search_query)).perform(clearText());
        onView(withId(R.id.btn_search)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.error_empty_search)));
    }

    @Test
    public void searchPicsWithUnknownText_nothingFoundMessage() throws InterruptedException {
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
    public void searchAndSwipe_pictureRemoved() throws InterruptedException {
        onView(withId(R.id.et_search_query))
                .perform(clearText())
                .perform(typeText(TEST_SEARCH), pressImeActionButton());
        Thread.sleep(2000);

        onView(withId(R.id.rv_images)).perform(actionOnItemAtPosition(0, swipeLeft()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.removed_from_list)));
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
    public void searchAndClickPicture_imageViewerVisible() throws InterruptedException {
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
    }


    // Gallery Fragment Test


    @Test
    public void takePhotoAndSwipe_photoRemovedFromList() {
        navigate_to(NAV_GALLERY);
        onView(withId(R.id.btn_make_photo)).perform(click());
        // Check Camera Fragment Visible
        onView(withId(R.id.previewView)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_take_shot)).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();
        onView(withId(R.id.rv_gallery_images)).check(new RecyclerViewItemCountAssertion(greaterThan(0)));
        onView(withId(R.id.rv_gallery_images)).perform(actionOnItemAtPosition(0, swipeLeft()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.removed_from_list)));
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
    public void navigateToSearchHist_searchHistFragmentVisible() {
        navigate_to(NAV_SEARCH_HISTORY);
        onView(withId(R.id.rv_search_history)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToMapsScreen_mapsFragmentVisible() {
        navigate_to(NAV_MAPS);
        onView(withId(R.id.btn_map_search)).check(matches(isDisplayed()));
    }

}