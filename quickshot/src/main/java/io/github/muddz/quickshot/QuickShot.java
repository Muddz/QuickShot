package io.github.muddz.quickshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import static android.os.Environment.DIRECTORY_PICTURES;

public class QuickShot {

    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_PNG = ".png";
    private static final String EXTENSION_NOMEDIA = ".nomedia";
    private static final int JPG_MAX_QUALITY = 100;

    private boolean printStackTrace;
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
     * <i>NOTE: For devices running Android 10 (+API 29) and above image files will now be saved relative to /Internal storage/Pictures/ due to 'Scoped storage'</i><br><br>
     * <p>Directories which don't already exist will be automatically created.</p>
     *
     * @param path if not set, path defaults to /Pictures/ regardless of any API level
     */
    public QuickShot setPath(String path) {
        this.path = path;
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

    /**
     * Enable QuickShot to log and print exception stacks
     */
    public QuickShot enableLogging() {
        printStackTrace = true;
        return this;
    }

    /**
     * Listen for successive or failure results when calling save()
     */
    public QuickShot setResultListener(@NonNull QuickShotListener listener) {
        this.listener = listener;
        if (listener == null) {
            throw new NullPointerException("QuickShot.setResultListener() was provided with a null object reference");
        }
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
     *
     * @throws NullPointerException if View is null.
     */
    public void save() throws NullPointerException {
        if (view instanceof SurfaceView) {
            PixelCopyHelper.getSurfaceBitmap((SurfaceView) view, new PixelCopyHelper.PixelCopyListener() {
                @Override
                public void onSurfaceBitmapReady(Bitmap surfaceBitmap) {
                    new BitmapSaver(getContext(), surfaceBitmap, printStackTrace, path, filename, fileExtension, jpgQuality, listener).execute();
                }

                @Override
                public void onSurfaceBitmapError(String errorMsg) {
                    listener.onQuickShotFailed(path, errorMsg);
                }
            });
        } else {
            new BitmapSaver(getContext(), getBitmap(), printStackTrace, path, filename, fileExtension, jpgQuality, listener).execute();
        }
    }

    public interface QuickShotListener {
        void onQuickShotSuccess(String path);

        void onQuickShotFailed(String path, String errorMsg);
    }

    static class BitmapSaver extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> weakContext;
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        private boolean printStacktrace;
        private int jpgQuality;
        private String errorMsg;
        private String path;
        private String filename;
        private String fileExtension;
        private Bitmap bitmap;
        private File file;
        private QuickShotListener listener;

        BitmapSaver(Context context, Bitmap bitmap, boolean printStacktrace, String path, String filename, String fileExtension, int jpgQuality, QuickShotListener listener) {
            this.weakContext = new WeakReference<>(context);
            this.bitmap = bitmap;
            this.printStacktrace = printStacktrace;
            this.path = path;
            this.filename = filename;
            this.fileExtension = fileExtension;
            this.jpgQuality = jpgQuality;
            this.listener = listener;
        }

        private void save() {
            if (path == null) {
                path = weakContext.get().getFilesDir() + File.separator + DIRECTORY_PICTURES;
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
                if (printStacktrace) {
                    e.printStackTrace();
                }
                errorMsg = e.toString();
                cancel(true);
            } finally {
                bitmap = null;
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            save();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            listener.onQuickShotSuccess(file.getAbsolutePath());
            if (!QuickShotUtils.isAboveAPI29()) {
                MediaScannerConnection.scanFile(weakContext.get(), new String[]{file.getAbsolutePath()}, null, null);
            }
        }

        @Override
        protected void onCancelled() {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onQuickShotFailed(file.getAbsolutePath(), errorMsg);
                }
            });
        }
    }
}
