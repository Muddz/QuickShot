package muddzdev.pixelshot.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import muddzdev.pixelshot.DrawingBoardView;
import muddzdev.pixelshot.R;

/**
 * Created by Muddz on 23-08-2017.
 */

public class ViewFragment extends BaseFragment implements DrawingBoardView.OnDrawingListener {

    private DrawingBoardView drawingBoardView;
    private LinearLayout drawHint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_fragment, container, false);
        drawingBoardView = v.findViewById(R.id.drawingview);
        drawingBoardView.setOnDrawingListener(this);
        drawHint = v.findViewById(R.id.drawhint);
        return v;
    }


    @Override
    public View getTargetView() {
        return drawingBoardView;
    }

    @Override
    public void onDrawingStarted() {
        drawHint.setVisibility(View.GONE);
    }
}
