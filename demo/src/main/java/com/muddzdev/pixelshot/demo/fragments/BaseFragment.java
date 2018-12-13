package com.muddzdev.pixelshot.demo.fragments;

import androidx.fragment.app.Fragment;
import android.view.View;

public abstract class BaseFragment extends Fragment {

    /**
     *
     * @return The View we want to save as an image.
     */
    public abstract View getTargetView();

}
