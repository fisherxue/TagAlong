package com.tagalong.tagalong;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mainActivityActivityTestRule.getActivity();
    }

    @Test
    public void testSignUp() {
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_button)).check(matches(isDisplayed()));

        /*
        onView(withId(R.id.username)).perform(typeText("wong12"));
        onView(withId(R.id.password)).perform(typeText("xd"));
        onView(withId(R.id.password2)).perform(typeText("xd"));
        closeSoftKeyboard();
        onView(withId(R.id.Email)).perform(typeText("chunru77@byronz.com"));
        closeSoftKeyboard();
        onView(withId(R.id.nextbutton)).perform(click());
        onView(withId(R.id.nextbutton)).check(matches(isDisplayed()));

        onView(withHint("First Name")).perform(typeText("Chun Ru"));
        onView(withId(R.id.lastName)).perform(typeText("Wong"));
        onView(withId(R.id.gender)).perform(typeText("Female"));
        onView(withId(R.id.age)).perform(typeText("96"));
        onView(withId(R.id.isDriver)).perform(click());
        onView(withId(R.id.seekMusic)).perform(swipeRight());
        onView(withId(R.id.seekSpeed)).perform(swipeRight());
        onView(withId(R.id.seekSmoking)).perform(swipeLeft());
        onView(withId(R.id.seekFragrance)).perform(swipeRight());
        onView(withId(R.id.seekChatting)).perform(swipeLeft());

         */
        //onView(withId(R.id.submit)).perform(click());
        //onView(withId(R.id.nav_chat)).perform(click());
        //onView(withText("Successfully Logged In")).check(matches(isDisplayed()));
        //onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));

    }
    @Test
    public void testChat() {
        onView(withId(R.id.userNameLogin)).perform(typeText("bwong"));
        onView(withId(R.id.passwordLogin)).perform(typeText("xd"));
        closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //onView(ViewMatchers.withId(R.id.home_frag_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, ))

    }

    @Test
    public void testLogin() {
        onView(withId(R.id.userNameLogin)).perform(typeText("bwong"));
        onView(withId(R.id.passwordLogin)).perform(typeText("xd"));
        closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        pressBack();
        //onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
        /*
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
        onView(withId(R.id.edit)).perform(click());
        onView(withId(R.id.firstName)).perform(typeText("Chun Ru"));
        onView(withId(R.id.lastName)).perform(typeText("Wong"));
        onView(withId(R.id.gender)).perform(typeText("Male"));
        onView(withId(R.id.age)).perform(typeText("69"));
        onView(withId(R.id.carCapacity)).perform(typeText("0"));
        onView(withId(R.id.seekMusic)).perform(swipeRight());
        onView(withId(R.id.seekSpeed)).perform(swipeRight());
        onView(withId(R.id.seekSmoking)).perform(swipeLeft());
        onView(withId(R.id.seekFragrance)).perform(swipeRight());
        onView(withId(R.id.seekChatting)).perform(swipeLeft());
        onView(withId(R.id.submit)).perform(click());
        onView(withText("YOUR PROFILE")).check(matches(isDisplayed()));


         */
    }


}
