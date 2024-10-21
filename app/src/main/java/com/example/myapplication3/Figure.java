package com.example.myapplication3;

import android.graphics.Bitmap;

public class Figure extends Entity{

    int health;
    int shield;

    public Figure(Bitmap bitmap, int x, int y, int with, int height, int health, int shield) {
        super(bitmap, x, y, with, height);
        this.health = health;
        this.shield = shield;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }
}
