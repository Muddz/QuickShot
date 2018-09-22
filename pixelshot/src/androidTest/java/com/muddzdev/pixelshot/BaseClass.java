package com.muddzdev.pixelshot;

import android.Manifest;
import android.graphics.Color;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.view.View;

import org.junit.Rule;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.view.View.MeasureSpec.EXACTLY;

/**
 * Currently not in use.
 */

public class BaseClass {

    static final String FORMAT_JPG = ".jpg";
    static final String FORMAT_PNG = ".png";
    static final String FORMAT_NOMEDIA = ".nomedia";

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);


    void deleteExistingDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
        if (directory.exists()) {
            directory.delete();
        }
    }

    View getTestView() {
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

    void sleepThread(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
