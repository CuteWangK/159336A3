package com.example.myapplication3;

import android.graphics.Bitmap;

public class Bullet extends Entity{
    int damage;

    public Bullet(Bitmap bitmap, int x, int y, int with, int height, int damage) {
        super(bitmap, x, y, with, height);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
