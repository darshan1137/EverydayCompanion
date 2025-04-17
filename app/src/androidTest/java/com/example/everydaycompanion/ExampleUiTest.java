package com.example.everydaycompanion;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;
import androidx.test.uiautomator.UiObject2;

import com.example.everydaycompanion.DBHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleUiTest {

    private static final String PACKAGE_NAME = "com.example.everydaycompanion";
    private static final int LAUNCH_TIMEOUT = 5000;

    private UiDevice device;

    @Before
    public void setUp() throws Exception {
        // Initialize UIAutomator
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Wake up screen
        if (!device.isScreenOn()) {
            device.wakeUp();
        }

        // Add test user to DB
        Context context = ApplicationProvider.getApplicationContext();
        DBHelper db = new DBHelper(context);
        db.insertUser("Test User","test@example.com", "password123");

        // Launch the app
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
        } else {
            throw new AssertionError("Unable to launch app.");
        }
    }

    @Test
    public void testLoginAndDashboard() {
        // Wait for either login screen or dashboard screen
        device.wait(Until.findObject(By.pkg(PACKAGE_NAME)), 3000);

        // Try to find email field
        UiObject2 emailField = device.findObject(By.res(PACKAGE_NAME, "et_email"));

        if (emailField != null) {
            // We're on login screen
            UiObject2 passwordField = device.findObject(By.res(PACKAGE_NAME, "et_password"));
            UiObject2 loginButton = device.findObject(By.res(PACKAGE_NAME, "btn_login"));

            assertNotNull("Password field not found", passwordField);
            assertNotNull("Login button not found", loginButton);

            emailField.setText("test@example.com");
            passwordField.setText("password123");
            loginButton.click();

            System.out.println("✅ Logged in via test credentials");

            // Wait for dashboard
            device.wait(Until.hasObject(By.res(PACKAGE_NAME, "gridLayout")), 5000);
        } else {
            System.out.println("🔁 Already logged in, skipping login step");
        }

        // Validate dashboard loaded
        UiObject2 dashboardGrid = device.findObject(By.res(PACKAGE_NAME, "gridLayout"));

        if (dashboardGrid == null) {
            System.out.println("❌ Dashboard grid not found after login or auto-login!");
        }

        assertNotNull("Dashboard grid not found. Check auto-login or layout ID.", dashboardGrid);
    }



    @Test
    public void testFlashlightToggle() throws InterruptedException {
        // Login first
        testLoginAndDashboard();

        // Wait for and click Flashlight item in GridLayout
        device.wait(Until.findObject(By.text("Flashlight")), 3000);
        UiObject2 flashlightCard = device.findObject(By.text("Flashlight"));
        assertNotNull("Flashlight card not found", flashlightCard);
        flashlightCard.click();

        // Wait for flashlight toggle button
        device.wait(Until.findObject(By.res(PACKAGE_NAME, "toggleFlashlightBtn")), 2000);
        UiObject2 toggleButton = device.findObject(By.res(PACKAGE_NAME, "toggleFlashlightBtn"));

        assertNotNull("Flashlight toggle button not found", toggleButton);

        // Click to toggle flashlight
        toggleButton.click();

        // Optional: wait briefly then toggle again to turn it off
        Thread.sleep(1000);
        toggleButton.click();
    }
}
