package com.example.myapplication3;

import android.graphics.Bitmap;

public class Bullet extends Entity{
    int damage;
    float Angle;
    public Bullet(Bitmap bitmap, float x, float y, float width, float height, int damage, float Angle) {
        super(bitmap, x, y, width, height);
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
