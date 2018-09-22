package com.muddzdev.pixelshot;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;

class PixelCopyHelper {

    public static void getSurfaceBitmap(@NonNull SurfaceView surfaceView, @NonNull final PixelCopyListener listener) {
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
                        listener.onSurfaceBitmapError();
                    }
                    handlerThread.quitSafely();
                }
            }, new Handler(handlerThread.getLooper()));
        } else {
            Log.d(PixelShot.class.getSimpleName(), "Saving an image of a SurfaceView is only supported from API 24");
        }
    }

    interface PixelCopyListener {
        void onSurfaceBitmapReady(Bitmap bitmap);
        void onSurfaceBitmapError();
    }
}
