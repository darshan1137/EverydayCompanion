package com.example.everydaycompanion;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.example.everydaycompanion.DBHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PerformanceTest {

    private static final String PACKAGE_NAME = "com.example.everydaycompanion";
    private static final int LAUNCH_TIMEOUT = 5000;

    private UiDevice device;

    @Before
    public void setUp() throws Exception {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Wake up screen if off
        if (!device.isScreenOn()) {
            device.wakeUp();
        }

        // Add test user
        Context context = ApplicationProvider.getApplicationContext();
        DBHelper db = new DBHelper(context);
        db.insertUser("Test User", "test@example.com", "password123");

        // Launch app
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        assertNotNull("Launch intent is null", intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void testAppStartupTime() {
        long startTime = System.currentTimeMillis();

        // Relaunch the app again to measure startup time
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
        long endTime = System.currentTimeMillis();
        long startupTime = endTime - startTime;

        Log.i("PerformanceTest", "✅ App Startup Time: " + startupTime + " ms");
        assertTrue("❌ App took too long to start", startupTime < 5000);
    }

    @Test
    public void testDashboardResponseTime() {
        // Wait for login or dashboard
        device.wait(Until.findObject(By.pkg(PACKAGE_NAME)), 3000);
        UiObject2 emailField = device.findObject(By.res(PACKAGE_NAME, "et_email"));

        long startTime = System.currentTimeMillis();

        if (emailField != null) {
            UiObject2 passwordField = device.findObject(By.res(PACKAGE_NAME, "et_password"));
            UiObject2 loginButton = device.findObject(By.res(PACKAGE_NAME, "btn_login"));

            assertNotNull("Password field not found", passwordField);
            assertNotNull("Login button not found", loginButton);

            emailField.setText("test@example.com");
            passwordField.setText("password123");
            loginButton.click();
        }

        // Wait for dashboard
        device.wait(Until.hasObject(By.res(PACKAGE_NAME, "gridLayout")), 5000);
        long endTime = System.currentTimeMillis();

        UiObject2 dashboardGrid = device.findObject(By.res(PACKAGE_NAME, "gridLayout"));
        assertNotNull("❌ Dashboard grid not found", dashboardGrid);

        long responseTime = endTime - startTime;
        Log.i("PerformanceTest", "✅ Dashboard Response Time: " + responseTime + " ms");
        assertTrue("❌ Dashboard load too slow", responseTime < 4000);
    }
}
