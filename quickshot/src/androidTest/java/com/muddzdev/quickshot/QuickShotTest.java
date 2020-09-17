package com.muddzdev.quickshot;

import android.Manifest;
import android.graphics.Color;
import android.view.View;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.view.View.MeasureSpec.EXACTLY;


@RunWith(AndroidJUnit4.class)
public class QuickShotTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    private View testView;

    @Before
    public void setup() {
        testView = generateTestView();
    }


    @Test
    public void testCallbackPathNotNull() {
        QuickShot.of(testView).setResultListener(new QuickShot.QuickShotListener() {
            @Override
            public void onQuickShotSuccess(String path) {
                Assert.assertNotNull(path);
            }

            @Override
            public void onQuickShotFailed(String path, String errorMsg) {

            }
        }).save();
        sleepThread();
    }


    @Test
    public void testIfSavedInJPG() {
        QuickShot.of(testView).setResultListener(new QuickShot.QuickShotListener() {
            @Override
            public void onQuickShotSuccess(String path) {
                Assert.assertTrue(path.contains(".jpg"));
            }

            @Override
            public void onQuickShotFailed(String path, String errorMsg) {

            }

        }).save();
        sleepThread();
    }

    @Test
    public void testIfSavedInPNG() {
        QuickShot.of(testView).toPNG().setResultListener(new QuickShot.QuickShotListener() {
            @Override
            public void onQuickShotSuccess(String path) {
                Assert.assertTrue(path.contains(".png"));
            }

            @Override
            public void onQuickShotFailed(String path, String errorMsg) {

            }
        }).save();
        sleepThread();
    }

    @Test
    public void testIfSavedInNomedia() {
        QuickShot.of(testView).toNomedia().setResultListener(new QuickShot.QuickShotListener() {
            @Override
            public void onQuickShotSuccess(String path) {
                Assert.assertTrue(path.contains(".nomedia"));
            }

            @Override
            public void onQuickShotFailed(String path, String errorMsg) {

            }
        }).save();
        sleepThread();
    }


    @Test
    public void testIfDirectoryWasCreated() {
        QuickShot.of(testView).setPath("QuickShotTestDirectory").setResultListener(new QuickShot.QuickShotListener() {
            @Override
            public void onQuickShotSuccess(String path) {
                File file = new File(path);
                File directory = new File(file.getParent());
                boolean isDirectory = directory.exists() && directory.isDirectory();
                Assert.assertTrue(isDirectory);
            }

            @Override
            public void onQuickShotFailed(String path, String errorMsg) {

            }
        }).save();
        sleepThread();
    }


    @Test
    public void testIfFileExist() {
        QuickShot.of(testView).setPath("QuickShotTestDirectory").setResultListener(new QuickShot.QuickShotListener() {
            @Override
            public void onQuickShotSuccess(String path) {
                File file = new File(path);
                boolean doFileExists = file.exists() && !file.isDirectory();
                Assert.assertTrue(doFileExists);
            }

            @Override
            public void onQuickShotFailed(String path, String errorMsg) {

            }
        }).save();
        sleepThread();
    }

    private View generateTestView() {
        int width = 950;
        int height = 950;

        int widthMS = View.MeasureSpec.makeMeasureSpec(width, EXACTLY);
        int heightMS = View.MeasureSpec.makeMeasureSpec(height, EXACTLY);

        View view = new View(InstrumentationRegistry.getTargetContext());
        view.measure(widthMS, heightMS);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setBackgroundColor(Color.GRAY);

        return view;
    }

    private void sleepThread() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
