package com.example.myapplication3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameController extends View {

    private Paint bgPaint;  // 摇杆背景的画笔
    private Paint handlePaint;  // 摇杆控制点的画笔
    private float centerX, centerY;  // 摇杆的中心点
    private float handleX, handleY;  // 摇杆控制点的位置
    private float radius;  // 摇杆的半径
    private float handleRadius;  // 控制点的半径
    private float angle = 0;  // 角度
    private float strength = 0;  // 力度

    public GameController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initController();
    }

    private void initController() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.GRAY);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(100);  // 设置背景的透明度 (0-255)，0完全透明，255完全不透明

        handlePaint = new Paint();
        handlePaint.setColor(Color.BLACK);
        handlePaint.setStyle(Paint.Style.FILL);
        handlePaint.setAlpha(150);  // 设置控制点的透明度 (0-255)

        // 初始化半径
        radius = 200;
        handleRadius = 60;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制摇杆背景
        canvas.drawCircle(centerX, centerY, radius, bgPaint);
        // 绘制摇杆控制点
        canvas.drawCircle(handleX, handleY, handleRadius, handlePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 设置摇杆的中心点为视图的中心
        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
        handleX = centerX;
        handleY = centerY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取触摸点的位置
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 计算触摸点和中心点之间的距离
                float dx = touchX - centerX;
                float dy = touchY - centerY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                // 如果触摸点在摇杆范围内，移动摇杆控制点
                if (distance < radius) {
                    handleX = touchX;
                    handleY = touchY;
                } else {
                    // 如果超出范围，计算边界上的点
                    handleX = centerX + (dx / distance) * radius;
                    handleY = centerY + (dy / distance) * radius;
                }

                // 更新角度和力度
                calculateAngleAndStrength();

                // 重绘视图
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // 触摸松开时，将控制点恢复到中心位置
                handleX = centerX;
                handleY = centerY;
                // 重置角度和力度
                angle = 0;
                strength = 0;

                invalidate();
                break;
        }

        return true;
    }

    private void calculateAngleAndStrength() {
        // 计算角度 (以弧度为单位)
        angle = (float) Math.atan2(handleY - centerY, handleX - centerX);
        // 计算力度 (归一化为 0 到 1 之间)
        strength = (float) Math.sqrt((handleX - centerX) * (handleX - centerX) + (handleY - centerY) * (handleY - centerY)) / radius;
        strength = Math.min(strength, 1);  // 确保力度不超过1
    }

    public float getAngle() {
        return angle;
    }

    public float getStrength() {
        return strength;
    }
}
