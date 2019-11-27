package com.muddzdev.quickshot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

class QuickShotUtils {

    static String getMimeType(String src) {
        src = src.substring(1);
        if (src.equals("jpg")) {
            src = "jpeg";
        }
        return "image" + File.separator + src;
    }

    static boolean isAndroidQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }


    //TODO Is it the libraries responsibility to handle this?
    private Bitmap generateLongBitmap(RecyclerView recyclerView) {

        int itemCount = recyclerView.getAdapter().getItemCount();
        RecyclerView.ViewHolder viewHolder = recyclerView.getAdapter().createViewHolder(recyclerView, 0);

        //Measure the sizes of list item views to find out how big itemView should be
        viewHolder.itemView.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // Define measured widths/heights
        int measuredItemHeight = viewHolder.itemView.getMeasuredHeight();
        int measuredItemWidth = viewHolder.itemView.getMeasuredWidth();

        //Set width/height of list item views
        viewHolder.itemView.layout(0, 0, measuredItemWidth, measuredItemHeight);

        //Create the Bitmap and Canvas to draw on
        Bitmap recyclerViewBitmap = Bitmap.createBitmap(recyclerView.getMeasuredWidth(), measuredItemHeight * itemCount, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(recyclerViewBitmap);

        //Draw RecyclerView Background:
        if (recyclerView.getBackground() != null) {
            Drawable drawable = recyclerView.getBackground().mutate();
            drawable.setBounds(measuredItemWidth, measuredItemHeight * itemCount, 0, 0);
            drawable.draw(canvas);
        }

        //Draw all list item views
        int viewHolderTopPadding = 0;
        for (int i = 0; i < itemCount; i++) {
            recyclerView.getAdapter().onBindViewHolder(viewHolder, i);
            viewHolder.itemView.setDrawingCacheEnabled(true);
            viewHolder.itemView.buildDrawingCache();
            canvas.drawBitmap(viewHolder.itemView.getDrawingCache(), 0f, viewHolderTopPadding, null);
            viewHolderTopPadding += measuredItemHeight;
            viewHolder.itemView.setDrawingCacheEnabled(false);
            viewHolder.itemView.destroyDrawingCache();

//            //TODO This should work but doesn't
//            recyclerView.getAdapter().onBindViewHolder(viewHolder, i);
//            viewHolder.itemView.draw(canvas);
//            canvas.drawBitmap(recyclerViewBitmap, 0f, viewHolderTopPadding, null);
//            viewHolderTopPadding += measuredItemHeight;
        }
        return recyclerViewBitmap;
    }

}

