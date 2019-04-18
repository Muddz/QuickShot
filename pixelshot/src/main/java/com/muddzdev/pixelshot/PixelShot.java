package com.muddzdev.pixelshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

/*
 * Copyright 2018 Muddi Walid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


public class PixelShot {

    private static final String TAG = PixelShot.class.getSimpleName();
    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_PNG = ".png";
    private static final String EXTENSION_NOMEDIA = ".nomedia";
    private static final int JPG_MAX_QUALITY = 100;

    private File path = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
    private String filename = String.valueOf(System.currentTimeMillis());
    private String fileExtension = EXTENSION_JPG;
    private int jpgQuality = JPG_MAX_QUALITY;
    private boolean isExternal = true;

    private PixelShotListener listener;
    private View view;

    private PixelShot(@NonNull View view) {
        this.view = view;
    }

    public static PixelShot of(@NonNull View view) {
        return new PixelShot(view);
    }

    public PixelShot setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public PixelShot setPath(File path, boolean isExternal) {
        this.path = path;
        this.isExternal = isExternal;
        return this;
    }

    public PixelShot setPath(File path) throws IOException {
        return setPath(path, path.getCanonicalPath().contains(
                Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator));
    }

    public PixelShot setPath(String path, boolean relativeToExternal, boolean isExternal) {
        this.isExternal = isExternal;
        if (relativeToExternal) {
            return setPath(new File(Environment.getExternalStorageDirectory(), path), isExternal);
        } else {
            return setPath(new File(path), isExternal);
        }
    }

    public PixelShot setPath(String path) {
        return setPath(path, true, true);
    }

    public PixelShot setResultListener(PixelShotListener listener) {
        this.listener = listener;
        return this;
    }

    public PixelShot toJPG() {
        jpgQuality = JPG_MAX_QUALITY;
        setFileExtension(EXTENSION_JPG);
        return this;
    }

    public PixelShot toJPG(int jpgQuality) {
        this.jpgQuality = jpgQuality;
        setFileExtension(EXTENSION_JPG);
        return this;
    }

    public PixelShot toPNG() {
        setFileExtension(EXTENSION_PNG);
        return this;
    }

    public PixelShot toNomedia() {
        setFileExtension(EXTENSION_NOMEDIA);
        return this;
    }

    /**
     * @throws NullPointerException If View is null.
     */

    public void save() throws NullPointerException {
        if (isExternal) {
            if (!Utils.isStorageReady()) {
                throw new IllegalStateException("Storage was not ready for use");
            }
            if (!Utils.isPermissionGranted(getAppContext())) {
                throw new SecurityException("Permission WRITE_EXTERNAL_STORAGE is missing");
            }
        }

        if (view instanceof SurfaceView) {
            PixelCopyHelper.getSurfaceBitmap((SurfaceView) view, new PixelCopyHelper.PixelCopyListener() {
                @Override
                public void onSurfaceBitmapReady(Bitmap surfaceBitmap) {
                    new BitmapSaver(getAppContext(), surfaceBitmap, path, filename, fileExtension, jpgQuality, isExternal, listener).execute();
                }

                @Override
                public void onSurfaceBitmapError() {
                    Log.d(TAG, "Couldn't create a bitmap of the SurfaceView");
                    if (listener != null) {
                        listener.onPixelShotFailed();
                    }
                }
            });
        } else {
            new BitmapSaver(getAppContext(), getViewBitmap(), path, filename, fileExtension, jpgQuality, isExternal, listener).execute();
        }
    }


    private void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    private Context getAppContext() {
        if (view == null) {
            throw new NullPointerException("The provided View was null");
        } else {
            return view.getContext().getApplicationContext();
        }
    }

    private Bitmap getViewBitmap() {
        Bitmap bitmap;
        if (view instanceof TextureView) {
            bitmap = ((TextureView) view).getBitmap();
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            canvas.setBitmap(null);
            return bitmap;
        } else {
            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            canvas.setBitmap(null);
            return bitmap;
        }
    }


    public interface PixelShotListener {
        void onPixelShotSuccess(String path);

        void onPixelShotFailed();
    }


    static class BitmapSaver extends AsyncTask<Void, Void, Void> implements MediaScannerConnection.OnScanCompletedListener {

        private final WeakReference<Context> weakContext;
        private Handler handler = new Handler(Looper.getMainLooper());
        private Bitmap bitmap;
        private File path;
        private String filename;
        private String fileExtension;
        private int jpgQuality;
        private PixelShotListener listener;
        private File file;
        private boolean runMediaScanner;

        BitmapSaver(Context context, Bitmap bitmap, File path, String filename, String fileExtension, int jpgQuality,
                    boolean runMediaScanner, PixelShotListener listener) {
            this.weakContext = new WeakReference<>(context);
            this.bitmap = bitmap;
            this.path = path;
            this.filename = filename;
            this.fileExtension = fileExtension;
            this.jpgQuality = jpgQuality;
            this.runMediaScanner = runMediaScanner;
            this.listener = listener;
        }

        private void cancelTask() {
            cancel(true);
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPixelShotFailed();
                    }
                });
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!path.exists() && !path.mkdirs()) {
                cancelTask();
                return null;
            }

            file = new File(path, filename + fileExtension);
            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
                switch (fileExtension) {
                    case EXTENSION_JPG:
                        bitmap.compress(Bitmap.CompressFormat.JPEG, jpgQuality, out);
                        break;
                    case EXTENSION_PNG:
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                cancelTask();
            }

            bitmap.recycle();
            bitmap = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (runMediaScanner) {
                MediaScannerConnection.scanFile(weakContext.get(), new String[]{file.getPath()}, null, this);
            } else {
                listener.onPixelShotSuccess(file.getPath());
            }
            weakContext.clear();
        }

        @Override
        public void onScanCompleted(final String path, final Uri uri) {
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (uri != null) {
                            Log.i(TAG, "Saved image to path: " + path);
                            Log.i(TAG, "Saved image to URI: " + uri);
                            listener.onPixelShotSuccess(path);
                        } else {
                            listener.onPixelShotFailed();
                        }
                    }
                });
            }
        }
    }
}

