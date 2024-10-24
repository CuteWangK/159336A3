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
    public Figure(Bitmap bitmap, float x, float y, float width, float height, int health, boolean isFacingRight) {
        super(bitmap, x, y, width, height);
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
