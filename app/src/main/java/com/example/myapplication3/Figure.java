package com.example.myapplication3;

import android.graphics.Bitmap;

public class Figure extends Entity{

    int health;

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setFacingRight(boolean facingRight) {
        isFacingRight = facingRight;
    }

    boolean isFacingRight;
    public Figure(Bitmap bitmap, int x, int y, int with, int height, int health, boolean isFacingRight) {
        super(bitmap, x, y, with, height);
        this.health = health;
        this.isFacingRight = isFacingRight;

    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

}
