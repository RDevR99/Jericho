package com.example.mad_assignment;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mainActivity = null;

    @Before
    public void setUp() throws Exception {
        mainActivity = mActivityTestRule.getActivity();
    }

    // TEST 1: Makes sure home fragment is loaded and that floating action button is displayed
    @Test
    public void testHomeFragment()
    {
        View floatingButton = mainActivity.findViewById(R.id.floatingActionButton);
        assertNotNull(floatingButton);
    }

    // TEST 2: UI Testing ==> Search Interface
    @Test
    public void testSearchFragment()
    {
        String query = "CSE2MAD";
        String queryResult = "Course Code: CSE2MAD";

        Espresso.onView(withId(R.id.navigation_search)).perform(click());
        Espresso.onView(withId(R.id.searchBar)).perform(typeText(query));
        Espresso.closeSoftKeyboard();
        //Espresso.onView(withId(R.id.CourseCode)).check(matches(withText(queryResult)));
        Espresso.onView(allOf(withId(R.id.CourseCode), withText(queryResult)));
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}