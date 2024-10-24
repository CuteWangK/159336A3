package com.example.myapplication3;

import android.graphics.Bitmap;

public class Bullet extends Entity{
    int damage;
    float Angle;
    public Bullet(Bitmap bitmap, int x, int y, int with, int height, int damage, float Angle) {
        super(bitmap, x, y, with, height);
        this.damage = damage;
        this.Angle = Angle;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
