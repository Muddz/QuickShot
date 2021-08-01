package io.github.muddz.quickshot.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


public class DrawingBoardView extends View {

    private OnDrawingListener listener;
    private Paint drawingPaint;
    private Path path = new Path();

    public DrawingBoardView(Context context) {
        super(context);
        setup();
    }

    public DrawingBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        setFocusableInTouchMode(true);
        setFocusable(true);
        drawingPaint = new Paint();
        drawingPaint.setColor(Color.BLACK);
        drawingPaint.setAntiAlias(true);
        drawingPaint.setStrokeWidth(convertToDP(5));
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeJoin(Paint.Join.ROUND);
        drawingPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, drawingPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xTouchPos = event.getX();
        float yTouchPos = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(xTouchPos, yTouchPos);
                path.lineTo(xTouchPos, yTouchPos);
                if (listener != null) {
                    listener.onDrawingStarted();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(xTouchPos, yTouchPos);
                break;
            default:
                return false;
        }
        postInvalidate();
        return true;
    }


    public void setOnDrawingListener(OnDrawingListener listener) {
        this.listener = listener;
    }

    private float convertToDP(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public interface OnDrawingListener {
        void onDrawingStarted();
    }
}
