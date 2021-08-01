package io.github.muddz.quickshot;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.PixelCopy;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

class PixelCopyHelper {

    static void getSurfaceBitmap(@NonNull SurfaceView surfaceView, @NonNull final PixelCopyListener listener) {
        final Bitmap bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);
        final HandlerThread handlerThread = new HandlerThread(PixelCopyHelper.class.getSimpleName());
        handlerThread.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PixelCopy.request(surfaceView, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
                @Override
                public void onPixelCopyFinished(int copyResult) {
                    if (copyResult == PixelCopy.SUCCESS) {
                        listener.onSurfaceBitmapReady(bitmap);
                    } else {
                        listener.onSurfaceBitmapError("Couldn't create bitmap of the SurfaceView");
                    }
                    handlerThread.quitSafely();
                }
            }, new Handler(handlerThread.getLooper()));
        } else {
            listener.onSurfaceBitmapError("Saving an image of a SurfaceView is only supported for API 24 and above");
        }
    }

    interface PixelCopyListener {
        void onSurfaceBitmapReady(Bitmap bitmap);

        void onSurfaceBitmapError(String errorMsg);
    }
}
