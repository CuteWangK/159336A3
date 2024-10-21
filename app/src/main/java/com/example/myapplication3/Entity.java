package com.example.myapplication3;

import android.graphics.Bitmap;

public class Entity {
    Bitmap bitmap;
    int x, y, with, height;


    public Entity(Bitmap bitmap, int x, int y, int with, int height) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.with = with;
        this.height = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWith() {
        return with;
    }

    public void setWith(int with) {
        this.with = with;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
