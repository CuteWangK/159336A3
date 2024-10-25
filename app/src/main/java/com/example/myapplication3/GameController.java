package com.example.myapplication3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameController extends View {

    private Paint bgPaint;
    private Paint handlePaint;
    private float centerX, centerY;
    private float handleX, handleY;
    private float radius;
    private float handleRadius;
    private float angle = 0;
    private float strength = 0;

    public GameController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initController();
    }

    private void initController() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.GRAY);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(100);

        handlePaint = new Paint();
        handlePaint.setColor(Color.BLACK);
        handlePaint.setStyle(Paint.Style.FILL);
        handlePaint.setAlpha(150);

        radius = 200;
        handleRadius = 60;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(centerX, centerY, radius, bgPaint);

        canvas.drawCircle(handleX, handleY, handleRadius, handlePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
        handleX = centerX;
        handleY = centerY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = touchX - centerX;
                float dy = touchY - centerY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < radius) {
                    handleX = touchX;
                    handleY = touchY;
                } else {

                    handleX = centerX + (dx / distance) * radius;
                    handleY = centerY + (dy / distance) * radius;
                }

                calculateAngleAndStrength();

                invalidate();
                break;

            case MotionEvent.ACTION_UP:

                handleX = centerX;
                handleY = centerY;

                angle = 0;
                strength = 0;

                invalidate();
                break;
        }

        return true;
    }

    private void calculateAngleAndStrength() {
        // Angle
        angle = (float) Math.atan2(handleY - centerY, handleX - centerX);
        // Strength
        strength = (float) Math.sqrt((handleX - centerX) * (handleX - centerX) + (handleY - centerY) * (handleY - centerY)) / radius;
        strength = Math.min(strength, 1); //less than 1
    }

    public float getAngle() {
        return angle;
    }

    public float getStrength() {
        return strength;
    }
}
