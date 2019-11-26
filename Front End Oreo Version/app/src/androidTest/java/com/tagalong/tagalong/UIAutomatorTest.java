package com.tagalong.tagalong;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 16)
public class UIAutomatorTest {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.tagalong.tagalong";

    private static final int LAUNCH_TIMEOUT = 5000;

    private static final String USERNAME = "bwong";

    private static final String DRIVER = "bwong1";

    private static final String PASSWORD = "xd";

    private UiDevice mDevice;

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the blueprint app
        Context context = getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }
    

    @Test
    public void testSetTrip() {
        // Type text and then press the button.
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "userNameLogin"))
                .setText(USERNAME);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "passwordLogin"))
                .setText(PASSWORD);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "login_button"))
                .click();

        // Verify the test is displayed in the Ui
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_maps")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_maps")).click();
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "search")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "search")).click();
        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));

        searchAutoComplete.perform(replaceText("Indigo Broadway"), closeSoftKeyboard());

        ViewInteraction searchAutoComplete2 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")), withText("Indigo Broadway"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete2.perform(pressImeActionButton());

        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "arrivalDate")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "arrivalDate"))
                .setText("30/11/2019");
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "arrivalTime")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "arrivalTime"))
                .setText("12:00");
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "To")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "To")).click();
        mDevice.waitForWindowUpdate(BASIC_SAMPLE_PACKAGE, LAUNCH_TIMEOUT);
        mDevice.openNotification();
        mDevice.waitForWindowUpdate(BASIC_SAMPLE_PACKAGE, LAUNCH_TIMEOUT);

        mDevice.pressBack();
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "logout")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "logout")).click();
    }

    @Test
    public void testExternalAPI() {
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "userNameLogin"))
                .setText(USERNAME);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "passwordLogin"))
                .setText(PASSWORD);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "login_button"))
                .click();

        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_chat")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_chat")).click();

        mDevice.waitForWindowUpdate(BASIC_SAMPLE_PACKAGE, LAUNCH_TIMEOUT);

        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "map")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "map")).click();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mDevice.pressBack();
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "logout")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "logout")).click();

    }

    @Test
    public void testComplexLogic() {
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "userNameLogin"))
                .setText(DRIVER);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "passwordLogin"))
                .setText(PASSWORD);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "login_button"))
                .click();

        // Verify the test is displayed in the Ui
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_maps")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_maps")).click();
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "search")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "search")).click();
        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));

        searchAutoComplete.perform(replaceText("Indigo Broadway"), closeSoftKeyboard());

        ViewInteraction searchAutoComplete2 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")), withText("Indigo Broadway"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete2.perform(pressImeActionButton());

        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "arrivalDate")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "arrivalDate"))
                .setText("30/11/2019");
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "arrivalTime")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "arrivalTime"))
                .setText("12:00");
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "To")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "To")).click();
        mDevice.waitForWindowUpdate(BASIC_SAMPLE_PACKAGE, LAUNCH_TIMEOUT);


        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_chat")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_chat")).click();

        mDevice.waitForWindowUpdate(BASIC_SAMPLE_PACKAGE, LAUNCH_TIMEOUT);

        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "accept")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "accept")).click();


        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_home")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "nav_home")).click();


        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "logout")), 500);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "logout")).click();
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = getApplicationContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private UiObject getNotificationStackScroller()
    {
        /*
         * access Notification Center through resource id, package name, class name.
         * if you want to check resource id, package name or class name of the specific view in the screen,
         * run 'uiautomatorviewer' from command.
         */
        UiSelector notificationStackScroller = new UiSelector().packageName("com.android.systemui")
                .className("android.view.ViewGroup")
                .resourceId(
                        "com.android.systemui:id/notification_stack_scroller");
        UiObject notificationStackScrollerUiObject = mDevice.findObject(notificationStackScroller);
        assertTrue(notificationStackScrollerUiObject.exists());

        return notificationStackScrollerUiObject;
    }
}
