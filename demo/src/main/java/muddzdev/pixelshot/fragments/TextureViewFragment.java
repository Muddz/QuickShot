package muddzdev.pixelshot.fragments;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import muddzdev.pixelshot.R;

/**
 * Created by Muddz on 23-08-2017.
 */

public class TextureViewFragment extends BaseFragment implements TextureView.SurfaceTextureListener {

    private Camera camera;
    private TextureView textureView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.texture_fragment, container, false);
        textureView = v.findViewById(R.id.textureview);
        textureView.setSurfaceTextureListener(this);
        return v;
    }

    @Override
    public View getTargetView() {
        return textureView;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        camera = Camera.open();
        camera.setDisplayOrientation(90);

        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size cameraSize = getOptimalPreviewSize(previewSizes, width, height);

        if (isAutoFocusSupported(camera)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        parameters.setPreviewSize(cameraSize.width, cameraSize.height);
        camera.setParameters(parameters);

        try {
            camera.setPreviewTexture(surface);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Solution credit: https://stackoverflow.com/a/19592492/9591909
     */


    private boolean isAutoFocusSupported(Camera camera) {
        if (camera != null) {
            for (String supportedMode : camera.getParameters().getSupportedFocusModes()) {
                if (supportedMode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        camera.stopPreview();
        camera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
