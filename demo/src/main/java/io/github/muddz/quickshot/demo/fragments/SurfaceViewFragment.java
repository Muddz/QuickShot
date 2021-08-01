package io.github.muddz.quickshot.demo.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.muddz.quickshot.demo.R;


/**
 * Created by Muddz on 23-08-2017.
 */

public class SurfaceViewFragment extends BaseFragment implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.surface_fragment, container, false);
        surfaceView = v.findViewById(R.id.surfaceview);
        surfaceView.getHolder().addCallback(this);
        Uri uri = Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/raw/" + "numbers");
        mediaPlayer = MediaPlayer.create(getContext(), uri);
        return v;
    }

    @Override
    public View getTargetView() {
        return surfaceView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setSurface(holder.getSurface());
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
