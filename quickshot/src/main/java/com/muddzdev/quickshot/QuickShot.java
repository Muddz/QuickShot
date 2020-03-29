package com.muddzdev.quickshot;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import static android.os.Environment.DIRECTORY_PICTURES;

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

public class QuickShot {

    private static final String TAG = QuickShot.class.getSimpleName();
    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_PNG = ".png";
    private static final String EXTENSION_NOMEDIA = ".nomedia";
    private static final int JPG_MAX_QUALITY = 100;

    private boolean saveInternal;
    private int jpgQuality = JPG_MAX_QUALITY;
    private String fileExtension = EXTENSION_JPG;
    private String filename = String.valueOf(System.currentTimeMillis());
    private String path;
    private Bitmap bitmap;
    private View view;
    private Context context;
    private QuickShotListener listener;


    private QuickShot(@NonNull View view) {
        this.view = view;
        this.context = view.getContext();
    }

    private QuickShot(@NonNull Bitmap bitmap, @NonNull Context context) {
        this.bitmap = bitmap;
        this.context = context;
    }

    public static QuickShot of(@NonNull View view) {
        return new QuickShot(view);
    }

    public static QuickShot of(@NonNull Bitmap bitmap, @NonNull Context context) {
        return new QuickShot(bitmap, context);
    }

    /**
     * @param filename if not set, filename defaults to a timestamp from {@link System#currentTimeMillis}
     */
    public QuickShot setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    /**
     * For devices running Android Q (+API 29) files will now be saved relative to /storage/Pictures/ in public storage due to Android's new 'Scooped storage'.
     * <p>Directories which don't already exist will be automatically created.</p>
     *
     * @param path if not set, path defaults to /Pictures/ regardless of any API level
     */
    public QuickShot setPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * Only for devices running Android Q/API 29 and higher!
     *
     * @param path relative to the apps internal storage
     */

    @RequiresApi(Build.VERSION_CODES.Q)
    public QuickShot setInternalPath(String path) {
        this.path = path;
        this.saveInternal = true;
        return this;
    }

    /**
     * Listen for successive or failure results when calling save()
     */
    public QuickShot setResultListener(QuickShotListener listener) {
        this.listener = listener;
        return this;
    }

    private void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Save as .jpg format in highest quality
     * default is .jpg
     */
    public QuickShot toJPG() {
        jpgQuality = JPG_MAX_QUALITY;
        setFileExtension(EXTENSION_JPG);
        return this;
    }

    /**
     * Save as .jpg format in a custom quality between 0-100
     * default is 100
     */
    public QuickShot toJPG(int jpgQuality) {
        this.jpgQuality = jpgQuality;
        setFileExtension(EXTENSION_JPG);
        return this;
    }

    /**
     * Save as .png format for lossless compression
     * default is .jpg
     */
    public QuickShot toPNG() {
        setFileExtension(EXTENSION_PNG);
        return this;
    }

    /**
     * Save as .nomedia for making the picture invisible for photo viewer apps and galleries.
     */
    public QuickShot toNomedia() {
        setFileExtension(EXTENSION_NOMEDIA);
        return this;
    }

    private Context getContext() {
        if (context == null) {
            throw new NullPointerException("Attempt to save the picture failed: View or Context was null");
        }
        return context;
    }

    private Bitmap getBitmap() {
        if (bitmap != null) {
            return bitmap;
        } else if (view instanceof TextureView) {
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


    /**
     * save() runs in a asynchronous thread
     * @throws NullPointerException if View is null.
     */

    //Lets make this prettier
    public void save() throws NullPointerException {
        if (view instanceof SurfaceView) {
            PixelCopyHelper.getSurfaceBitmap((SurfaceView) view, new PixelCopyHelper.PixelCopyListener() {
                @Override
                public void onSurfaceBitmapReady(Bitmap surfaceBitmap) {
                    new BitmapSaver(getContext(), surfaceBitmap, saveInternal, path, filename, fileExtension, jpgQuality, listener).execute();
                }

                @Override
                public void onSurfaceBitmapError() {
                    if (listener != null) {
                        listener.onQuickShotFailed(path);
                    }
                }
            });
        } else {
            new BitmapSaver(getContext(), getBitmap(), saveInternal, path, filename, fileExtension, jpgQuality, listener).execute();
        }
    }

    public interface QuickShotListener {
        void onQuickShotSuccess(String path);

        void onQuickShotFailed(String path);
    }

    static class BitmapSaver extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> weakContext;
        private Handler handler = new Handler(Looper.getMainLooper());
        private QuickShotListener listener;
        private boolean success = true;
        private boolean saveInternal;
        private int jpgQuality;
        private String path;
        private String filename;
        private String fileExtension;
        private Bitmap bitmap;
        private File file;

        BitmapSaver(Context context, Bitmap bitmap, boolean saveInternal, String path, String filename, String fileExtension, int jpgQuality, QuickShotListener listener) {
            this.weakContext = new WeakReference<>(context);
            this.bitmap = bitmap;
            this.saveInternal = saveInternal;
            this.path = path;
            this.filename = filename;
            this.fileExtension = fileExtension;
            this.jpgQuality = jpgQuality;
            this.listener = listener;
        }

        private void save() {
            if (path == null) {
                path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORY_PICTURES;
            }
            File directory = new File(path);
            directory.mkdirs();
            file = new File(directory, filename + fileExtension);
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
                success = false;
                cancel(true);
            } finally {
                bitmap.recycle();
                bitmap = null;
            }
        }

        private void saveScoopedStorage() {
            path = (path != null ? (DIRECTORY_PICTURES + File.separator + path) : DIRECTORY_PICTURES);
            file = new File(path, filename + fileExtension); //only used for success/fail listeners
            ContentResolver resolver = weakContext.get().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, QuickShotUtils.getMimeType(fileExtension));
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri == null) {
                success = false;
                String msg = String.format("ContentResolver couldn't create URI for filename: %s and path: %s", filename, path);
                Log.e(TAG, msg);
                return;
            }

            try (OutputStream out = resolver.openOutputStream(imageUri)) {
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
                resolver.delete(imageUri, null, null);
                success = false;
                cancel(true);
            } finally {
                bitmap.recycle();
                bitmap = null;
            }
        }

        @Override
        protected void onCancelled() {
            if (listener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onQuickShotFailed(file.getAbsolutePath());
                    }
                });
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (QuickShotUtils.isAboveAPI29() && !saveInternal) {
                saveScoopedStorage();
            } else {
                save();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (listener == null) {
                return;
            }

            if (success) {
                listener.onQuickShotSuccess(file.getAbsolutePath());
                if (!QuickShotUtils.isAboveAPI29()) {
                    MediaScannerConnection.scanFile(weakContext.get(), new String[]{file.getAbsolutePath()}, null, null);
                }
            } else {
                listener.onQuickShotFailed(file.getAbsolutePath());
            }
        }
    }
}

