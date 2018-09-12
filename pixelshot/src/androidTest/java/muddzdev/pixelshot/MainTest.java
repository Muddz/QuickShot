package muddzdev.pixelshot;

import android.Manifest;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainTest {

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
    }


    @Test
    public void useAppContext() {

        Log.d("PIXELSHOT", "11111");

        PixelShot.of(getTestView()).setResultListener(new PixelShot.PixelShotListener() {
            @Override
            public void onPixelShotSuccess(String path) {
                Log.d("PIXELSHOT", "22222");
            }

            @Override
            public void onPixelShotFailed() {
                Log.d("PIXELSHOT", "33333");
            }
        }).save();

        Log.d("PIXELSHOT", "444444");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private View getTestView() {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        Canvas canvas = new Canvas();

//        Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.gallery_thumb);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        canvas.drawText("TEST", 50, 50, paint);
//        canvas.drawBitmap(srcBitmap, 50, 50, paint);

        View view = new View(context);
        view.setMinimumWidth(screenWidth);
        view.setMinimumHeight(screenHeight);
        view.draw(canvas);
        return view;
    }

    @After
    public void teardown() {

        //TODO DELETE TEST FILES AND PATHS
    }
}
