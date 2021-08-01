package io.github.muddz.quickshot.demo.fragments;

import android.view.View;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    /**
     * @return The View we want to save as an image.
     */
    public abstract View getTargetView();

}
