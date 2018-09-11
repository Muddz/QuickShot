package muddzdev.pixelshot.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

public abstract class BaseFragment extends Fragment {

    /**
     *
     * @return The View we want to save as an image.
     */
    public abstract View getTargetView();

}
